package com.bupt.memes.service;

import com.bupt.memes.config.AppConfig;
import com.bupt.memes.exception.AppException;
import com.bupt.memes.model.HNSWItem;
import com.bupt.memes.model.common.FileUploadResult;
import com.bupt.memes.model.transport.Embedding;
import com.bupt.memes.service.Interface.Storage;
import com.bupt.memes.util.KafkaUtil;
import com.bupt.memes.util.Preconditions;
import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.SearchResult;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.bupt.memes.aspect.Audit.INSTANCE_UUID;

@Component
@Slf4j
public class AnnIndexService {

    @Value("${hnsw.dimension}")
    private int dimension = 768;

    @Value("${hnsw.m}")
    private int m = 16;

    @Value("${hnsw.efSearch}")
    private int efSearch = 200;

    @Value("${hnsw.efConstruction}")
    private int efConstruction = 200;

    @Value("${hnsw.maxElements}")
    private int maxElements = 1000000;

    private long initIndexVersion = 0;
    private long initIndexSize = 0;

    private HnswIndex<String, float[], HNSWItem, Float> index;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private final AtomicBoolean consumerHealthy = new AtomicBoolean(false);

    private final Storage storage;

    private final AppConfig config;

    public AnnIndexService(Storage storage, AppConfig config) {
        this.storage = storage;
        this.config = config;
    }

    public List<SearchResult<HNSWItem, Float>> search(float[] vector, int topK) {
        Preconditions.checkNotNull(index, AppException.databaseError("HNSWIndex is not initialized"));
        try {
            readLock.lock();
            return index.findNearest(vector, topK);
        } finally {
            readLock.unlock();
        }
    }

    public List<SearchResult<HNSWItem, Float>> search(String key, int topK) {
        Preconditions.checkNotNull(index, AppException.databaseError("HNSWIndex is not initialized"));
        try {
            readLock.lock();
            return index.findNeighbors(key, topK);
        } finally {
            readLock.unlock();
        }
    }

    public void reloadIndex(long targetVersion, String indexFile, boolean forceReload) {
        if (forceReload || initIndexVersion < targetVersion) {
            log.info("Reloading index from file: {}", indexFile);
            setIndex(indexFile, targetVersion);
            log.info("Reloaded index with version: {}", initIndexVersion);
        }
    }

    public void add(String key, float[] vector) {
        Preconditions.checkNotNull(index, AppException.databaseError("HNSWIndex is not initialized"));
        HNSWItem hnswItem = new HNSWItem();
        hnswItem.setId(key);
        hnswItem.setVector(vector);
        try {
            writeLock.lock();
            boolean added = index.add(hnswItem);
            if (!added) {
                log.warn("Failed to add item to index, key: {}, maybe the key already exists", key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @SneakyThrows
    @SuppressWarnings("unused")
    public void saveIndex(String indexFile) {
        Preconditions.checkNotNull(index, AppException.databaseError("HNSWIndex is not initialized"));
        try {
            writeLock.lock();
            index.save(Path.of(indexFile));
        } finally {
            writeLock.unlock();
        }
    }

    public void initKafkaConsumer() {
        if (index == null) {
            log.error("Failed to init kafka consumer, index is not initialized,will retry later");
            return;
        }
        if (consumerHealthy.get()) {
            return;
        }
        if (consumerHealthy.compareAndSet(false, true)) {
            Thread.ofVirtual().start(this::listenKafka);
            log.info("Started kafka consumer for embedding");
        }
    }

    private void setNewIndex(HnswIndex<String, float[], HNSWItem, Float> newIndex, Long indexVersion) {
        writeLock.lock();
        index = newIndex;
        this.initIndexVersion = indexVersion;
        this.initIndexSize = index.size();
        writeLock.unlock();
    }

    private void setIndex(String indexFile, Long indexVersion) {
        /*
         * 从本地加载索引
         */
        HnswIndex<String, float[], HNSWItem, Float> loadedFromLocal = loadFromLocal(indexFile);
        if (loadedFromLocal != null) {
            setNewIndex(loadedFromLocal, indexVersion);
            log.info("Loaded index from local file: {}, size: {}", indexFile, index.size());
            return;
        }

        /*
         * 从网络加载索引
         */
        HnswIndex<String, float[], HNSWItem, Float> loadFromNet = loadFromNet(indexFile);
        if (loadFromNet != null) {
            setNewIndex(loadFromNet, indexVersion);
            log.info("Loaded index from network: {}, size: {}", indexFile, index.size());
            return;
        }

        /*
         * 初始化新的索引
         */
        HnswIndex<String, float[], HNSWItem, Float> newIndex = HnswIndex.newBuilder(dimension, DistanceFunctions.FLOAT_EUCLIDEAN_DISTANCE, maxElements)
                .withM(m)
                .withEf(efSearch)
                .withEfConstruction(efConstruction)
                .withRemoveEnabled()
                .build();
        setNewIndex(newIndex, 0L);
        log.info("Initialized new index with dimension: {}, m: {}, efSearch: {}, efConstruction: {}, maxElements: {}",
                dimension, m, efSearch, efConstruction, maxElements);
    }

    @SneakyThrows
    private HnswIndex<String, float[], HNSWItem, Float> loadFromNet(String url) {
        try {
            URI uri = new URI(url);
            Path path = Path.of("%s.index".formatted(INSTANCE_UUID));
            FileUtils.copyURLToFile(uri.toURL(), path.toFile());
            return HnswIndex.load(path);
        } catch (Exception e) {
            log.error("Failed to load index from network: {}", url);
            return null;
        }
    }

    private HnswIndex<String, float[], HNSWItem, Float> loadFromLocal(String indexFile) {
        try {
            String[] split = indexFile.split("/");
            indexFile = split[split.length - 1];
            Path path = Path.of(indexFile);
            if (!Files.exists(path)) {
                return null;
            }
            return HnswIndex.load(path);
        } catch (Exception e) {
            log.error("Failed to load index from local file: {}", indexFile);
            return null;
        }
    }

    private void listenKafka() {
        KafkaConsumer<String, byte[]> consumer;
        try {
            consumer = KafkaUtil.getConsumer(KafkaUtil.EMBEDDING);
        } catch (Exception e) {
            log.error("Failed to init kafka consumer for embedding", e);
            consumerHealthy.set(false);
            return;
        }

        while (consumerHealthy.get()) {
            // 离线批量索引构建+Kafka 实时增量索引
            // 离线每天凌晨全量索引构建
            var records = consumer.poll(Duration.ofSeconds(1));
            for (var record : records) {
                try {
                    Embedding embedding = Embedding.parseFrom(record.value());
                    List<Float> dataList = embedding.getDataList();
                    float[] vector = new float[dataList.size()];
                    for (int i = 0; i < dataList.size(); i++) {
                        vector[i] = dataList.get(i);
                    }
                    add(embedding.getId(), vector);
                    log.info("Added embedding to index, key: {}", embedding.getId());
                } catch (InvalidProtocolBufferException e) {
                    log.warn("Failed to parse embedding from kafka message,key:{}", record.key());
                } catch (Exception e) {
                    consumerHealthy.compareAndSet(true, false);
                }
            }
        }
    }

    /**
     * 把内存中的索引持久化到存储中
     *
     * @return 返回持久化后的索引地址
     */
    @SneakyThrows
    public Pair<Long, String> persistIndex() {
        Preconditions.checkNotNull(storage, AppException.databaseError("Storage is not initialized"));
        long diff = index.size() - initIndexSize;
        if (diff < config.indexPersistThreshold) {
            log.info("Index size is less than threshold, current diff: {}, threshold: {}", diff, config.indexPersistThreshold);
            return null;
        }
        log.info("Persisting index to storage, current diff: {}, threshold: {}", diff, config.indexPersistThreshold);
        var now = System.currentTimeMillis();
        var indexName = "%s.index".formatted(now);
        saveIndex(indexName);
        log.info("Saved index to local file: {}", indexName);
        byte[] bytes = FileUtils.readFileToByteArray(Path.of(indexName).toFile());
        FileUploadResult resp = storage.store(bytes, "application/octet-stream", "embeddings/%s-%s".formatted(INSTANCE_UUID, indexName));
        log.info("Persisted index to storage, version: {}, url: {}", now, resp.url());
        setIndex(resp.url(), now);
        return Pair.of(now, resp.url());
    }
}

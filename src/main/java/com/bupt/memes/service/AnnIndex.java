package com.bupt.memes.service;

import com.bupt.memes.model.HNSWItem;
import com.bupt.memes.model.transport.Embedding;
import com.bupt.memes.util.KafkaUtil;
import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.SearchResult;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.bupt.memes.aspect.Audit.instanceUUID;

@Component
public class HNSWIndex {

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

    @Value("${hnsw.indexFile}")
    private String indexFile = "hnsw.index";
    private int indexVersion = 20;

    private HnswIndex index;
    private final Logger logger = LoggerFactory.getLogger(HNSWIndex.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final AtomicBoolean kafkaAlive = new AtomicBoolean(false);


    private void setNewIndex(HnswIndex<String, float[], HNSWItem, Float> newIndex) {
        writeLock.lock();
        index = newIndex;
        writeLock.unlock();
    }

    @SneakyThrows
    public void initIndex() {

        HnswIndex<String, float[], HNSWItem, Float> loadedFromLocal = loadFromLocal(indexFile);
        if (loadedFromLocal != null) {
            setNewIndex(loadedFromLocal);
            logger.info("Loaded index from local file: {}, size: {}", indexFile, index.size());
            return;
        }

        HnswIndex<String, float[], HNSWItem, Float> loadFromNet = loadFromNet(indexFile);
        if (loadFromNet != null) {
            setNewIndex(loadFromNet);
            logger.info("Loaded index from network: {}, size: {}", indexFile, index.size());
            return;
        }


        HnswIndex<String, float[], HNSWItem, Float> newIndex =
                HnswIndex.newBuilder(dimension, DistanceFunctions.FLOAT_EUCLIDEAN_DISTANCE, maxElements)
                        .withM(m)
                        .withEf(efSearch)
                        .withEfConstruction(efConstruction)
                        .build();

        writeLock.lock();
        index = newIndex;
        writeLock.unlock();
        logger.info("Initialized new index with dimension: {}, m: {}, efSearch: {}, efConstruction: {}, maxElements: {}",
                dimension, m, efSearch, efConstruction, maxElements);
        Thread.ofVirtual().start(this::watchKafka);

    }

    @SneakyThrows
    private HnswIndex<String, float[], HNSWItem, Float> loadFromNet(String url) {
        URI uri = new URI(url);
        Path path = Path.of(instanceUUID + ".index");
        FileUtils.copyURLToFile(uri.toURL(), path.toFile());
        return HnswIndex.load(path);
    }

    @SneakyThrows
    private HnswIndex<String, float[], HNSWItem, Float> loadFromLocal(String indexFile) {
        Path path = Path.of(indexFile);
        if (!Files.exists(path)) {
            return null;
        }
        return HnswIndex.load(path);
    }

    @SneakyThrows
    public synchronized void saveIndex() {
        if (index != null) {
            index.save(Path.of(indexFile));
        } else {
            logger.error("HNSWIndex is not initialized. Cannot save.");
        }
    }

    public List<SearchResult> search(float[] vector, int topK) {
        if (index == null) {
            logger.error("HNSWIndex is not initialized");
            return List.of();
        }
        try {
            readLock.lock();
            return index.findNearest(vector, topK);
        } finally {
            readLock.unlock();
        }
    }

    public void add(HNSWItem item) {
        if (index == null) {
            logger.error("HNSWIndex is not initialized");
            return;
        }
        try {
            writeLock.lock();
            index.add(item);
        } finally {
            writeLock.unlock();
        }
    }

    public synchronized void reloadIndex(int targetVersion, boolean forceReload) {
        if (forceReload || indexVersion < targetVersion) {
            initIndex();
            indexVersion = targetVersion;
        }
    }

    private void watchKafka() {
        if (!kafkaAlive.compareAndSet(false, true)) {
            logger.info("Kafka consumer is already running");
            return;
        }
        KafkaConsumer<String, byte[]> consumer = KafkaUtil.getConsumer("embedding", instanceUUID);
        do {
            try {
                var records = consumer.poll(Duration.ZERO);
                for (var record : records) {
                    Embedding embedding = Embedding.parseFrom(record.value());
                    HNSWItem item = new HNSWItem();
                    float[] vector = new float[dimension];
                    for (int i = 0; i < dimension; i++) {
                        vector[i] = embedding.getData(i);
                    }
                    item.setId(embedding.getId());
                    item.setVector(vector);
                    add(item);
                    logger.debug("Added item: {}", item.getId());
                }
            } catch (Exception e) {
                logger.error("Error while consuming messages", e);
                consumer = null;
            }
        } while (consumer != null);
    }

    public static void main(String[] args) {
        HNSWIndex index = new HNSWIndex();
        index.initIndex();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            HNSWItem item = new HNSWItem();
            item.setId(String.valueOf(random.nextInt()));
            float[] vector = new float[index.dimension];
            for (int j = 0; j < index.dimension; j++) {
                vector[j] = random.nextFloat();
            }
            item.setVector(vector);
            index.index.add(item);
        }

        index.saveIndex();
        System.out.println(index.index.size());
    }
}

package com.bupt.memes.service;

import com.bupt.memes.model.HNSWItem;
import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.SearchResult;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.bupt.memes.aspect.Audit.instanceUUID;


@Component
@Data
@Accessors(chain = true)
public class AnnIndex {

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
    private long indexVersion = 0;


    private HnswIndex<String, float[], HNSWItem, Float> index;
    private final Logger logger = LoggerFactory.getLogger(AnnIndex.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private void setNewIndex(HnswIndex<String, float[], HNSWItem, Float> newIndex) {
        writeLock.lock();
        index = newIndex;
        writeLock.unlock();
    }

    public void initIndex(String indexFile) {

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

        initIndex();
    }

    public void initIndex() {
        HnswIndex<String, float[], HNSWItem, Float> newIndex = HnswIndex.newBuilder(dimension, DistanceFunctions.FLOAT_EUCLIDEAN_DISTANCE, maxElements).withM(m).withEf(efSearch).withEfConstruction(efConstruction).build();
        writeLock.lock();
        index = newIndex;
        writeLock.unlock();
        logger.info("Initialized new index with dimension: {}, m: {}, efSearch: {}, efConstruction: {}, maxElements: {}", dimension, m, efSearch, efConstruction, maxElements);
    }

    @SneakyThrows
    private HnswIndex<String, float[], HNSWItem, Float> loadFromNet(String url) {
        try {
            URI uri = new URI(url);
            Path path = Path.of(instanceUUID + ".index");
            FileUtils.copyURLToFile(uri.toURL(), path.toFile());
            return HnswIndex.load(path);
        } catch (Exception e) {
            logger.error("Failed to load index from network: {}", url);
            return null;
        }
    }

    private HnswIndex<String, float[], HNSWItem, Float> loadFromLocal(String indexFile) {
        try {
            Path path = Path.of(indexFile);
            if (!Files.exists(path)) {
                return null;
            }
            return HnswIndex.load(path);
        } catch (Exception e) {
            logger.error("Failed to load index from local file: {}", indexFile);
            return null;
        }
    }


    public List<SearchResult<HNSWItem, Float>> search(float[] vector, int topK) {
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

    public List<SearchResult<HNSWItem, Float>> search(String key, int topK) {
        if (index == null) {
            logger.error("HNSWIndex is not initialized");
            return List.of();
        }
        try {
            readLock.lock();
            return index.findNeighbors(key, topK);
        } finally {
            readLock.unlock();
        }
    }

    public void reloadIndex(long targetVersion, String indexFile, boolean forceReload) {
        if (forceReload || indexVersion < targetVersion) {
            logger.info("Reloading index from file: {}", indexFile);
            initIndex(indexFile);
            indexVersion = targetVersion;
            logger.info("Reloaded index with version: {}", indexVersion);
        }
    }

    public void add(String key, float[] vector) {
        if (index == null) {
            logger.error("HNSWIndex is not initialized");
            return;
        }
        HNSWItem hnswItem = new HNSWItem();
        hnswItem.setId(key);
        hnswItem.setVector(vector);
        try {
            writeLock.lock();
            index.add(hnswItem);
        } finally {
            writeLock.unlock();
        }
    }

    @SneakyThrows
    public void saveIndex(String indexFile) {
        if (index == null) {
            logger.error("HNSWIndex is not initialized");
            return;
        }
        try {
            writeLock.lock();
            index.save(Path.of(indexFile));
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        AnnIndex annIndex = new AnnIndex();
        annIndex.initIndex("hnsw.index");
        for (SearchResult<HNSWItem, Float> neighbor : (annIndex.index.findNeighbors("66233f9d73d05a71e01a6462", 100))) {
            System.out.println(neighbor.item().getId() + " " + neighbor.distance());
        }
    }
}

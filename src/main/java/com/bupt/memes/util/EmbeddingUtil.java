package com.bupt.memes.util;

import com.bupt.memes.model.HNSWItem;
import com.bupt.memes.model.transport.Embedding;
import com.bupt.memes.model.transport.Embeddings;
import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public class EmbeddingUtil {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(EmbeddingUtil.class);

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            logger.error("Usage: EmbeddingUtil <path> <dimension>");
            return;
        }

        String path = args[0];
        int dimension = Integer.parseInt(args[1]);
        int maxElements = 1000000;
        int m = 16;
        int efConstruction = 200;
        int efSearch = 200;

        // read as a byte array
        byte[] bytes = FileUtils.readFileToByteArray(new File(path));
        Embeddings embeddings = Embeddings.parseFrom(bytes);
        if (embeddings.getCount() != embeddings.getDataList().size()) {
            logger.error("Invalid embeddings file, count not match data size");
            return;
        }

        HnswIndex<String, float[], HNSWItem, Float> newIndex = HnswIndex
                .newBuilder(dimension, DistanceFunctions.FLOAT_EUCLIDEAN_DISTANCE, maxElements)
                .withM(m)
                .withEf(efSearch)
                .withEfConstruction(efConstruction)
                .withRemoveEnabled()
                .build();

        for (Embedding embedding : embeddings.getDataList()) {
            newIndex.add(HNSWItem.builder().id(embedding.getId()).vector(toFloatArray(embedding)).build());
        }
        logger.info("Index built, saving to disk...");
        String indexName = String.format("%d-%d.index", System.currentTimeMillis(), embeddings.getCount());
        newIndex.save(new File(indexName));
        logger.info("Index saved to " + indexName);

        // load index
        HnswIndex<String, float[], HNSWItem, Float> loadedIndex = HnswIndex.load(new File(indexName));
        logger.info("Index loaded from " + indexName);
        // search
        String queryId = embeddings.getData(0).getId();
        Embedding query = embeddings.getData(0);
        logger.info("Searching for " + queryId);
        loadedIndex.findNearest(toFloatArray(query), 10).forEach(item -> System.out.println(item.item().getId() + " " + item.distance()));
        logger.info("Search done, bye~");
    }

    private static float[] toFloatArray(Embedding embedding) {
        float[] floats = new float[embedding.getDataList().size()];
        for (int i = 0; i < embedding.getDataList().size(); i++) {
            floats[i] = embedding.getData(i);
        }
        return floats;
    }

}

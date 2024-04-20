package com.bupt.memes.model;

import com.github.jelmerk.knn.Item;

public class HNSWSearchResult implements Item<String, float[]> {

    String id;
    float[] vector;

    @Override
    public String id() {
        return id;
    }

    @Override
    public float[] vector() {
        return vector;
    }

    @Override
    public int dimensions() {
        return vector.length == 0 ? 0 : vector.length;
    }

    @Override
    public long version() {
        return Item.super.version();
    }
}

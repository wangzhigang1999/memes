package com.memes.model;

import com.github.jelmerk.knn.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class HNSWItem implements Item<String, float[]> {

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
        return vector.length;
    }

    @Override
    public long version() {
        return Item.super.version();
    }
}

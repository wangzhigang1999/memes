package com.memes.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigItem {

    public enum Type {
        STRING, INTEGER, BOOLEAN, DOUBLE, JSON
    }

    String id;
    String key;
    String value;
    String description;
    boolean visible;
    String visibleName;
    Type type;
}

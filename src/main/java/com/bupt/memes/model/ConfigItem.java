package com.bupt.memes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("config")
public class ConfigItem {

    public enum Type {
        STRING,
        INTEGER,
        BOOLEAN,
        DOUBLE,
        JSON
    }

    String id;
    String key;
    String value;
    String description;
    boolean visible;
    String visibleName;
    Type type;
}

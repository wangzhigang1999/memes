package com.bupt.memes.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document("log")
public class LogDocument {
    private String url;
    private String method;
    private String ip;
    private Map<String, String[]> parameterMap;
    private String uuid;
    private long timecost; // ms
    private long timestamp;
    private String instanceUUID;

}
package com.bupt.dailyhaha.pojo;

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
    private String classMethod;
    private Object detail;
    private Map<String, String[]> parameterMap;
    private String uuid;
    private int status;
    private long timecost; // ms
    private long timestamp;
    private String env;
    private String instanceUUID;

}
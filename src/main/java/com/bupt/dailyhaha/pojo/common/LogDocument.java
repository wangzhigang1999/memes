package com.bupt.dailyhaha.pojo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LogDocument {
    private String url;
    private String method;
    private String ip;
    private String classMethod;

    private String detail;
    private Map<String, String[]> parameterMap;
    private String uuid;
    private int status;
    private long timecost; // ms
    private long timestamp;
    private String env;

    private String instanceUUID;

    public static LogDocument random() {
        return new LogDocument()
                .setUrl("http://localhost:8080/api/v1/log")
                .setMethod("GET")
                .setIp("1.1.1.1")
                .setClassMethod("com.bupt.dailyhaha.controller.LogController.log")
                .setDetail("detail")
                .setUuid("uuid")
                .setStatus(200)
                .setTimecost(100)
                .setTimestamp(System.currentTimeMillis())
                .setEnv("dev")
                .setInstanceUUID("instanceUUID")
                .setParameterMap(Map.of("key", new String[]{"value"}));
    }

    public static void main(String[] args) {
        System.out.println(LogDocument.random());
    }
}
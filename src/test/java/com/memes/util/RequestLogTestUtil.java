package com.memes.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.memes.model.pojo.RequestLog;

public class RequestLogTestUtil {

    private static final Random RANDOM = new Random();

    public static List<RequestLog> generateRequestLogs(int n) {
        List<RequestLog> requestLogs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            requestLogs.add(generateRandomRequestLog());
        }
        return requestLogs;
    }

    private static RequestLog generateRandomRequestLog() {
        return RequestLog
            .builder()
            .url(randomUrl())
            .method(randomHttpMethod())
            .ip(randomIp())
            .userAgent(randomUserAgent())
            .refer(randomRefer())
            .headers(randomHeaders())
            .parameterMap(randomParameterMap())
            .uuid(randomUuid())
            .responseStatus(randomResponseStatus())
            .responseSize(randomResponseSize())
            .timecost(randomTimecost())
            .timestamp(System.currentTimeMillis())
            .instanceUuid(randomInstanceUuid())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private static String randomUrl() {
        return "https://example.com/%s".formatted(randomString(10));
    }

    private static RequestLog.HttpMethod randomHttpMethod() {
        RequestLog.HttpMethod[] methods = RequestLog.HttpMethod.values();
        return methods[RANDOM.nextInt(methods.length)];
    }

    private static String randomIp() {
        return String
            .format(
                "%d.%d.%d.%d",
                RANDOM.nextInt(256),
                RANDOM.nextInt(256),
                RANDOM.nextInt(256),
                RANDOM.nextInt(256));
    }

    private static String randomUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";
    }

    private static String randomRefer() {
        return "https://referrer.com/%s".formatted(randomString(4));
    }

    private static String randomHeaders() {
        return "{\"Accept\": \"application/json\", \"Content-Type\": \"application/json\"}";
    }

    private static String randomParameterMap() {
        return "{\"param1\": \"value1\", \"param2\": \"value2\"}";
    }

    private static String randomUuid() {
        return java.util.UUID.randomUUID().toString();
    }

    private static Integer randomResponseStatus() {
        return RANDOM.nextInt(500) + 100; // Random status between 100 and 599
    }

    private static Long randomResponseSize() {
        return (long) (RANDOM.nextDouble() * 1024 * 1024); // Random size up to 1MB
    }

    private static Integer randomTimecost() {
        return RANDOM.nextInt(10000); // Random time cost up to 10 seconds
    }

    private static String randomInstanceUuid() {
        return java.util.UUID.randomUUID().toString();
    }

    private static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (RANDOM.nextInt(26) + 'a'));
        }
        return sb.toString();
    }
}

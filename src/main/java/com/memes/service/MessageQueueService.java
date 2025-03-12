package com.memes.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageQueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Gson gson = new Gson();

    public MessageQueueService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 发送消息
    public Long sendMessage(String queue, Object obj) {
        return sendMessage(queue, gson.toJson(obj));
    }

    // 发送消息
    public Long sendMessage(String queue, String message) {
        log.info("Sending message to queue {}: {}", queue, message);
        return redisTemplate.opsForList().rightPush(queue, message);
    }

    // 接收单个消息
    public String receiveMessage(String queue) {
        String message = redisTemplate.opsForList().leftPop(queue);
        if (message != null) {
            log.info("Received message from queue {}: {}", queue, message);
        }
        return message;
    }

    // 泛型化接收消息
    public <T> Optional<T> receiveMessage(String queue, Class<T> clazz) {
        return Optional
            .ofNullable(receiveMessage(queue))
            .map(message -> {
                try {
                    return gson.fromJson(message, clazz);
                } catch (Exception e) {
                    log.error("Failed to deserialize message: {}", message, e);
                    return null;
                }
            });
    }

    // 批量接收消息（String 类型）
    public List<String> receiveMessages(String queue, int count) {
        List<String> messages = redisTemplate.opsForList().leftPop(queue, count);
        if (messages != null && !messages.isEmpty()) {
            log.info("Received {} messages from queue {}", messages.size(), queue);
        }
        return Optional.ofNullable(messages).orElse(Collections.emptyList());
    }

    // 批量接收消息（泛型）
    public <T> List<T> receiveMessages(String queue, int count, Class<T> clazz) {
        List<String> messages = receiveMessages(queue, count);
        if (messages.isEmpty()) {
            return Collections.emptyList();
        }
        return messages
            .stream()
            .map(message -> deserializeMessage(message, clazz))
            .collect(Collectors.toList());
    }

    // 反序列化消息
    private <T> T deserializeMessage(String message, Class<T> clazz) {
        try {
            return gson.fromJson(message, clazz);
        } catch (Exception e) {
            log.error("Failed to deserialize message: {}", message, e);
            return null;
        }
    }
}

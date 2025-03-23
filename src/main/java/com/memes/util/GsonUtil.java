package com.memes.util;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GsonUtil {

    /**
     * -- GETTER -- 获取 Gson 实例 (如果需要自定义配置)
     */
    @Getter
    private static final Gson gson = createGson();

    /**
     * 创建配置好的Gson实例
     */
    private static Gson createGson() {
        return new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();
    }

    /**
     * 将对象转换为 JSON 字符串 (包含 null 值)
     *
     * @param obj
     *            要转换的对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            log.error("对象转换为 JSON 失败: ", e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串转换为对象
     */
    public static <T> T fromJson(String message, Class<T> clazz) {
        try {
            return gson.fromJson(message, clazz);
        } catch (Exception e) {
            log.error("Failed to deserialize message: {}", message, e);
            return null;
        }
    }

    /**
     * LocalDateTime 的序列化和反序列化适配器
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }

    /**
     * LocalDate 的序列化和反序列化适配器
     */
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }

    /**
     * LocalTime 的序列化和反序列化适配器
     */
    private static class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

        @Override
        public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            return LocalTime.parse(json.getAsString(), formatter);
        }
    }
}

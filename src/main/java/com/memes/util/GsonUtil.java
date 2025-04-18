package com.memes.util;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 创建配置好的 Gson 实例
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
            log.error("对象转换为 JSON 失败：", e);
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
            return null;
        }
    }

    /**
     * LocalDateTime 的序列化和反序列化适配器
     */
    private static class LocalDateTimeAdapter
        implements
            JsonSerializer<LocalDateTime>,
            JsonDeserializer<LocalDateTime> {
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

    /**
     * 从模型输出中提取 JSON 字符串，最常见的 ```json``` 如果没有，则尝试从文本中提取合法的 JSON 如果都没有，返回原字符串
     */
    public static String extractJsonFromModelOutput(String modelOut) {
        if (modelOut == null || modelOut.trim().isEmpty()) {
            return modelOut;
        }

        // 1. 首先尝试匹配常见的 ```json ``` 包裹的 JSON
        Pattern codeBlockPattern = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)\\s*```", Pattern.CASE_INSENSITIVE);
        Matcher codeBlockMatcher = codeBlockPattern.matcher(modelOut);

        if (codeBlockMatcher.find()) {
            String candidate = codeBlockMatcher.group(1).trim();
            if (isValidJson(candidate)) {
                return candidate;
            }
        }

        // 2. 如果没有找到代码块，尝试在整个字符串中寻找 JSON 对象或数组
        Pattern jsonPattern = Pattern.compile("\\{(?:[^{}]|\\{(?:[^{}]|\\{[^{}]*})*})*}|\\[[^]]*]");
        Matcher jsonMatcher = jsonPattern.matcher(modelOut);

        while (jsonMatcher.find()) {
            String candidate = jsonMatcher.group().trim();
            if (isValidJson(candidate)) {
                return candidate;
            }
        }

        // 3. 如果都不是，返回原字符串
        return modelOut;
    }

    /**
     * 简单检查字符串是否是有效的 JSON
     */
    private static boolean isValidJson(String str) {
        String trimmed = str.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        char firstChar = trimmed.charAt(0);
        char lastChar = trimmed.charAt(trimmed.length() - 1);

        // 必须是 {} 或 [] 包裹
        if (!((firstChar == '{' && lastChar == '}') || (firstChar == '[' && lastChar == ']'))) {
            return false;
        }

        // 简单的平衡括号检查
        int braceCount = 0;
        int bracketCount = 0;
        boolean inString = false;

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);

            if (c == '"' && (i == 0 || trimmed.charAt(i - 1) != '\\')) {
                inString = !inString;
            }

            if (!inString) {
                if (c == '{')
                    braceCount++;
                if (c == '}')
                    braceCount--;
                if (c == '[')
                    bracketCount++;
                if (c == ']')
                    bracketCount--;

                if (braceCount < 0 || bracketCount < 0) {
                    return false; // 括号不匹配
                }
            }
        }

        return braceCount == 0 && bracketCount == 0;
    }

}

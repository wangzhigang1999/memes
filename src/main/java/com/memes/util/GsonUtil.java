package com.memes.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class GsonUtil {

    /**
     * -- GETTER -- 获取 Gson 实例 (如果需要自定义配置)
     */
    @Getter
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

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
     * 将对象转换为格式化的 JSON 字符串
     *
     * @param obj
     *            要转换的对象
     * @return 格式化的 JSON 字符串
     */
    public static String toPrettyJson(Object obj) {
        try {
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            return prettyGson.toJson(obj);
        } catch (Exception e) {
            log.error("对象转换为格式化的 JSON 失败: ", e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串转换为对象
     *
     * @param json
     *            JSON 字符串
     * @param clazz
     *            目标对象的 Class 对象
     * @param <T>
     *            目标对象的类型
     * @return 转换后的对象，如果转换失败则返回 null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            log.error("JSON 字符串转换为对象失败 (Class): ", e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串转换为对象
     *
     * @param json
     *            JSON 字符串
     * @param typeOfT
     *            目标对象的 Type 对象，用于处理泛型
     * @param <T>
     *            目标对象的类型
     * @return 转换后的对象，如果转换失败则返回 null
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            log.error("JSON 字符串转换为对象失败 (Type): ", e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串转换为 List 集合
     *
     * @param json
     *            JSON 字符串
     * @param clazz
     *            集合中元素的 Class 对象
     * @param <T>
     *            集合中元素的类型
     * @return 转换后的 List 集合，如果转换失败则返回 null
     */
    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        try {
            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            log.error("JSON 字符串转换为 List 失败: ", e);
            return null;
        }
    }

    /**
     * 将JSON数组字符串转换为List<Map<String,Object>>
     *
     * @param json
     *            JSON数组字符串
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> fromJsonArrayToListMap(String json) {
        try {
            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            log.error("JSON 数组字符串转换为 List<Map<String,Object>> 失败: ", e);
            return null;
        }
    }

    /**
     * 将 JSON 字符串转换为 Map
     *
     * @param json
     *            JSON字符串
     * @param keyType
     *            Map Key的类型
     * @param valueType
     *            Map Value的类型
     * @param <K>
     *            Key 类型
     * @param <V>
     *            Value 类型
     * @return Map 对象
     */
    public static <K, V> Map<K, V> fromJsonMap(String json, Type keyType, Type valueType) {
        try {
            Type mapType = TypeToken.getParameterized(Map.class, keyType, valueType).getType();
            return gson.fromJson(json, mapType);
        } catch (JsonSyntaxException e) {
            log.error("JSON 字符串转换为 Map 失败: ", e);
            return null;
        }
    }

    /**
     * 从 JSON 字符串中获取指定 key 的值 (String 类型)
     *
     * @param json
     *            JSON 字符串
     * @param key
     *            要获取值的 key
     * @return key 对应的字符串值，如果 key 不存在或值为 null，则返回 null
     */
    public static String getString(String json, String key) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsString();
            }
            return null;
        } catch (JsonParseException e) {
            log.error("获取 JSON 字符串中的 String 失败: ", e);
            return null;
        }
    }

    /**
     * 从 JSON 字符串中获取指定 key 的值 (int 类型)
     *
     * @param json
     *            JSON 字符串
     * @param key
     *            要获取值的 key
     * @return key 对应的 int 值，如果 key 不存在或值为 null，则返回 0
     */
    public static int getInt(String json, String key) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsInt();
            }
            return 0;
        } catch (JsonParseException e) {
            log.error("获取 JSON 字符串中的 Int 失败: ", e);
            return 0;
        }
    }

    /**
     * 从 JSON 字符串中获取指定 key 的值 (boolean 类型)
     *
     * @param json
     *            JSON 字符串
     * @param key
     *            要获取值的 key
     * @return key 对应的 boolean 值，如果 key 不存在或值为 null，则返回 false
     */
    public static boolean getBoolean(String json, String key) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsBoolean();
            }
            return false;
        } catch (JsonParseException e) {
            log.error("获取 JSON 字符串中的 Boolean 失败: ", e);
            return false;
        }
    }

    /**
     * 从 JSON 字符串中获取 JsonArray
     *
     * @param json
     *            JSON 字符串
     * @param key
     *            要获取JsonArray 的 key
     * @return JsonArray, 如果key 不存在或为null, 则返回 null
     */
    public static JsonArray getJsonArray(String json, String key) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.getAsJsonArray(key);
            }
            return null;
        } catch (JsonParseException e) {
            log.error("获取 JSON 字符串中的 JsonArray 失败: ", e);
            return null;
        }
    }

    /**
     * 从 JSON 字符串中获取 JsonObject
     *
     * @param json
     *            JSON 字符串
     * @param key
     *            要获取JsonObject 的 key
     * @return JsonObject, 如果key 不存在或为null, 则返回 null
     */
    public static JsonObject getJsonObject(String json, String key) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.getAsJsonObject(key);
            }
            return null;
        } catch (JsonParseException e) {
            log.error("获取 JSON 字符串中的 JsonObject 失败: ", e);
            return null;
        }
    }

    /**
     * 将 JsonArray 转换为 List<String>
     *
     * @param jsonArray
     *            JsonArray 对象
     * @return List<String>
     */
    public static List<String> jsonArrayToListString(JsonArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (JsonElement element : jsonArray) {
                list.add(element.getAsString());
            }
        }
        return list;
    }

}

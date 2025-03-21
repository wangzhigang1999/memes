package com.memes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
}

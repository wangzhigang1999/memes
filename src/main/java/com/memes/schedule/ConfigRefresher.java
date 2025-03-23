package com.memes.schedule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.memes.annotation.DynamicConfig;
import com.memes.config.AppConfig;
import com.memes.model.pojo.Config;
import com.memes.service.ConfigService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置刷新器：负责定期从数据库拉取最新配置并应用到 AppConfig
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigRefresher {

    private final ConfigService configService;
    private final AppConfig appConfig;

    // 缓存 DynamicConfig 注解的字段
    private final Map<Field, DynamicConfig> annotatedFields = new ConcurrentHashMap<>();

    // 缓存 DynamicConfig 注解的方法
    private final Map<Method, DynamicConfig> annotatedMethods = new ConcurrentHashMap<>();

    /**
     * 初始化缓存
     */
    public void initCache() {
        log.info("正在初始化配置刷新器缓存...");

        // 缓存注解字段
        Arrays
            .stream(AppConfig.class.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(DynamicConfig.class))
            .forEach(field -> annotatedFields.put(field, field.getAnnotation(DynamicConfig.class)));

        // 缓存注解方法
        Arrays
            .stream(AppConfig.class.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(DynamicConfig.class))
            .forEach(method -> annotatedMethods.put(method, method.getAnnotation(DynamicConfig.class)));

        log.info("配置刷新器缓存初始化完成，字段: {}，方法: {}", annotatedFields.size(), annotatedMethods.size());
    }

    /**
     * 每30秒刷新一次配置
     */
    @Scheduled(fixedRate = 5000)
    public void refreshConfig() {
        // 懒加载初始化缓存
        if (annotatedFields.isEmpty() && annotatedMethods.isEmpty()) {
            initCache();
        }

        log.debug("开始刷新应用配置...");
        refreshFields();
        refreshMethods();
        log.debug("应用配置刷新完成");
    }

    /**
     * 刷新字段配置
     */
    private void refreshFields() {
        annotatedFields.forEach((field, annotation) -> {
            String key = annotation.key();
            Config config = configService.getConfig(key);
            if (config != null) {
                try {
                    applyConfigToField(field, config);
                } catch (Exception e) {
                    log.error("刷新配置到字段时出错: {}", key, e);
                }
            }
        });
    }

    /**
     * 刷新方法配置
     */
    private void refreshMethods() {
        annotatedMethods.forEach((method, annotation) -> {
            String key = annotation.key();
            Config config = configService.getConfig(key);
            if (config != null) {
                invokeConfigMethod(method, config.getValue());
            }
        });
    }

    /**
     * 应用配置值到字段
     */
    private void applyConfigToField(Field field, Config config) throws IllegalAccessException {
        boolean accessible = field.canAccess(appConfig);
        field.setAccessible(true);

        switch (config.getType()) {
            case BOOLEAN:
                field.setBoolean(appConfig, Boolean.parseBoolean(config.getValue()));
                break;
            case INTEGER:
                field.setInt(appConfig, Integer.parseInt(config.getValue()));
                break;
            case JSON:
                // JSON 类型通常通过 setter 方法处理
                break;
            case STRING:
            default:
                field.set(appConfig, config.getValue());
        }

        field.setAccessible(accessible);
    }

    /**
     * 调用配置方法
     */
    private void invokeConfigMethod(Method method, String value) {
        try {
            method.invoke(appConfig, value);
        } catch (Exception e) {
            log.error("调用配置方法时出错: {}", method.getName(), e);
        }
    }
}

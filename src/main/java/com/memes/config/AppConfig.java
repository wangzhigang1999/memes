package com.memes.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.memes.annotation.DynamicConfig;
import com.memes.model.pojo.Config;
import com.memes.service.ConfigService;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用配置，支持动态更新 简单的配置条目使用注解标记字段，复杂的配置条目使用注解标记方法
 */
@Slf4j
@Component("appConfig")
@Data
@Getter
public class AppConfig {
    private final ConfigService configService;
    @DynamicConfig(key = "bot.up", desc = "机器人是否开启", defaultValue = "true", type = Config.Type.BOOLEAN, visibleName = "爬虫开启")
    private boolean botUp;

    @DynamicConfig(key = "submission.num.min", desc = "每天的最少投稿数", defaultValue = "50", visibleName = "最少投稿数")
    private int minSubmissions = 50;

    @DynamicConfig(key = "submission.fetch.limit", desc = "每次获取的最大投稿数", defaultValue = "20", visibleName = "fetch size")
    private int subFetchLimit = 20;

    @DynamicConfig(key = "server.down", desc = "服务器是否停止服务", defaultValue = "false", type = Config.Type.BOOLEAN, visibleName = "停止服务")
    private boolean serverDown = false;

    public AppConfig(ConfigService configService) {
        this.configService = configService;
    }

    @PostConstruct
    public void init() {
        log.info("正在初始化应用配置...");
        initFields();
        initMethods();
        log.info("应用配置初始化完成");
    }

    private void initFields() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            DynamicConfig annotation = field.getAnnotation(DynamicConfig.class);
            if (annotation == null) {
                continue;
            }

            String key = annotation.key();
            String defaultValue = annotation.defaultValue();

            // 检查配置是否存在
            Config config = configService.getConfig(key);
            if (config == null) {
                // 不存在，创建新配置
                config = buildConfig(annotation);
                configService.save(config);
                log.info("初始化配置: {} = {}", key, defaultValue);
            } else {
                // 存在，应用现有配置
                try {
                    applyConfigToField(field, config);
                } catch (Exception e) {
                    log.error("应用配置到字段时出错: {}", key, e);
                }
            }
        }
    }

    private void initMethods() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            DynamicConfig annotation = method.getAnnotation(DynamicConfig.class);
            if (annotation == null) {
                continue;
            }

            String key = annotation.key();
            String defaultValue = annotation.defaultValue();

            // 检查配置是否存在
            Config config = configService.getConfig(key);
            if (config == null) {
                // 不存在，创建新配置
                config = buildConfig(annotation);
                configService.save(config);
                log.info("初始化配置: {} = {}", key, defaultValue);
                invokeConfigMethod(method, defaultValue);
            } else {
                // 存在，应用现有配置
                invokeConfigMethod(method, config.getValue());
            }
        }
    }

    private Config buildConfig(DynamicConfig annotation) {
        return Config
            .builder()
            .configKey(annotation.key())
            .value(annotation.defaultValue())
            .type(annotation.type())
            .description(annotation.desc())
            .visible(annotation.visible())
            .visibleName(annotation.visibleName())
            .build();
    }

    private void applyConfigToField(Field field, Config config) throws IllegalAccessException {
        boolean accessible = field.canAccess(this);
        field.setAccessible(true);

        switch (config.getType()) {
            case BOOLEAN:
                field.setBoolean(this, Boolean.parseBoolean(config.getValue()));
                break;
            case INTEGER:
                field.setInt(this, Integer.parseInt(config.getValue()));
                break;
            case JSON:
                // JSON 类型通常通过 setter 方法处理
                break;
            case STRING:
            default:
                field.set(this, config.getValue());
        }

        field.setAccessible(accessible);
    }

    private void invokeConfigMethod(Method method, String value) {
        try {
            method.invoke(this, value);
        } catch (Exception e) {
            log.error("调用配置方法时出错: {}", method.getName(), e);
        }
    }
}

package com.bupt.memes.service;

import com.bupt.memes.anno.DynamicConfig;
import com.bupt.memes.model.ConfigItem;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Lazy(false)
@Component("configUpdater")
@Slf4j
public class ConfigUpdater implements CommandLineRunner {
    private static ApplicationContext context;

    private static final Map<String, Method> methodMap = new ConcurrentHashMap<>();
    private static final Map<String, Field> fieldMap = new ConcurrentHashMap<>();

    private static final Set<ConfigItem> configItemSet = new HashSet<>();

    private final ApplicationContext applicationContext;
    private final MongoTemplate mongoTemplate;

    private final ConversionService conversionService;

    public ConfigUpdater(ApplicationContext applicationContext, MongoTemplate mongoTemplate, ConversionService conversionService) {
        this.applicationContext = applicationContext;
        this.mongoTemplate = mongoTemplate;
        this.conversionService = conversionService;
    }

    @SneakyThrows
    public boolean notifyUpdate(String key, String value) {
        if (methodMap.containsKey(key)) {
            Method method = methodMap.get(key);
            Object bean = context.getBean(method.getDeclaringClass());

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1) {
                Object convertedValue = conversionService.convert(value, parameterTypes[0]);
                method.invoke(bean, convertedValue);
            } else {
                log.error("Method {} should have only one parameter", method.getName());
                return false;
            }
            return true;
        }

        if (fieldMap.containsKey(key)) {
            Field field = fieldMap.get(key);
            Object bean = context.getBean(field.getDeclaringClass());
            Class<?> type = field.getType();
            Object convertedValue = conversionService.convert(value, type);
            field.set(bean, convertedValue);
            return true;
        }
        return false;
    }

    @Override
    public void run(String... args) {
        context = applicationContext;
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            Class<?> clazz = bean.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(DynamicConfig.class)) {
                    DynamicConfig annotation = method.getAnnotation(DynamicConfig.class);
                    method.setAccessible(true);
                    String key = annotation.key().isEmpty() ? method.getName() : annotation.key();
                    addConfigItem(key, annotation);

                    methodMap.put(key, method);
                }
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(DynamicConfig.class)) {
                    DynamicConfig annotation = field.getAnnotation(DynamicConfig.class);
                    field.setAccessible(true);
                    String key = annotation.key().isEmpty() ? field.getName() : annotation.key();
                    addConfigItem(key, annotation);
                    fieldMap.put(key, field);
                }
            }
        }
        initConfig();
        log.info("ConfigUpdater initialized");
    }

    private static void addConfigItem(String key, DynamicConfig dynamicConfig) {
        String desc = dynamicConfig.desc();
        String defaultValue = dynamicConfig.defaultValue();
        ConfigItem.Type type = dynamicConfig.type();
        boolean visible = dynamicConfig.visible();

        String name = dynamicConfig.visibleName();
        if (name.isEmpty()) {
            name = key.toUpperCase(Locale.ROOT);
        }

        ConfigItem build = ConfigItem.builder()
                .key(key)
                .description(desc)
                .value(defaultValue)
                .type(type)
                .visible(visible)
                .visibleName(name)
                .build();
        log.info("Add config item: {}", build);
        configItemSet.add(build);
    }

    private void initConfig() {
        for (ConfigItem configItem : configItemSet) {
            String key = configItem.getKey();
            Criteria criteria = Criteria.where("key").is(key);
            ConfigItem item = mongoTemplate.findOne(Query.query(criteria), ConfigItem.class);
            if (item == null) {
                mongoTemplate.save(configItem);
            }
        }
    }
}

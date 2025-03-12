package com.memes.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.memes.model.pojo.Config;

/**
 * 用来标记动态配置的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DynamicConfig {
    String key() default "";

    String desc() default "";

    String defaultValue() default "";

    boolean visible() default true;

    String visibleName() default "";

    Config.Type type() default Config.Type.INTEGER;
}

package com.bupt.memes.anno;

import com.bupt.memes.model.ConfigItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DynamicConfig {
    String key() default "";

    String desc() default "";

    String defaultValue() default "";

    boolean visible() default true;

    String visibleName() default "";

    ConfigItem.Type type() default ConfigItem.Type.INTEGER;
}

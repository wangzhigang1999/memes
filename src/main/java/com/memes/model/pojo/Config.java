package com.memes.model.pojo;

import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import lombok.Builder;
import lombok.Data;

@Data
@TableName(value = "config", autoResultMap = true)
@Builder
public class Config {
    private Integer id;
    private String configKey;
    private String value;
    private String description;
    private Boolean visible;
    private String visibleName;
    private Type type;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> constraints; // 直接映射 JSON

    public enum Type {
        STRING, INTEGER, BOOLEAN, DOUBLE, JSON
    }
}

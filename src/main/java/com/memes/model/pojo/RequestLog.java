package com.memes.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("request_log")
public class RequestLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String url;

    @EnumValue
    private HttpMethod method;

    private String ip;

    private String userAgent;

    private String refer;

    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String headers;

    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String parameterMap;

    private String uuid;

    private Integer responseStatus;

    private Long responseSize;

    private Integer timecost;

    private Long timestamp;

    private String instanceUuid;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
    }
}

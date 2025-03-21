package com.memes.model.pojo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class MediaContent {
    @TableId(type = IdType.AUTO)
    private Long id;

    private DataType dataType;

    private String dataContent;

    private String userId;

    private String checksum;

    private String llmDescription;

    private AiModerationStatus llmModerationStatus;

    private String rejectionReason;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    private Long fileSize;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    private ContentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum DataType {
        IMAGE, VIDEO, MARKDOWN
    }

    public enum AiModerationStatus {
        PENDING, APPROVED, REJECTED, FLAGGED
    }

    public enum ContentStatus {
        PENDING, APPROVED, REJECTED, DELETED
    }
}

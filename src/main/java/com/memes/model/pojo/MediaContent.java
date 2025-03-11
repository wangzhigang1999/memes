package com.memes.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaContent {
    private Integer id;

    private DataType dataType;

    private String dataContent;

    private String userId;

    private String checksum;

    private String llmDescription;

    private AiModerationStatus aiModerationStatus;

    private String rejectionReason;

    private List<String> tags;

    private Long fileSize;

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

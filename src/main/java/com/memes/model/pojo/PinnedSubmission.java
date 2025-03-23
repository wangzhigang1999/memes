package com.memes.model.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 置顶投稿记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("pinned_submission")
public class PinnedSubmission implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 被置顶的投稿ID
     */
    @TableField("submission_id")
    private Long submissionId;

    /**
     * 置顶时间
     */
    @TableField("pinned_at")
    private LocalDateTime pinnedAt;

    /**
     * 置顶过期时间
     */
    @TableField("pinned_expiry")
    private LocalDateTime pinnedExpiry;

    /**
     * 置顶排序优先级（数字越小越靠前）
     */
    @TableField("pinned_order")
    private Integer pinnedOrder;

    /**
     * 置顶原因或备注
     */
    @TableField("pinned_reason")
    private String pinnedReason;

    /**
     * 执行置顶操作的人
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

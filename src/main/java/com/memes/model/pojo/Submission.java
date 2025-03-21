package com.memes.model.pojo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
public class Submission {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> mediaContentIdList;

    private Integer likesCount;

    private Integer dislikesCount;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<String> tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<MediaContent> mediaContentList;
}

package com.memes.model.pojo;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
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
    private Integer id;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> mediaContentIdList;

    private Integer likesCount;

    private Integer dislikesCount;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<String> tags;
}

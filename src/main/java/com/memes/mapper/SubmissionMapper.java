package com.memes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memes.model.pojo.Submission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {
} 
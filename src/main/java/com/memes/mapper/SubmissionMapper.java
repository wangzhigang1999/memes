package com.memes.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memes.model.pojo.Submission;

@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {
}

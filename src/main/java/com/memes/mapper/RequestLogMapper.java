package com.memes.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memes.model.pojo.RequestLog;

@Mapper
public interface RequestLogMapper extends BaseMapper<RequestLog> {
}

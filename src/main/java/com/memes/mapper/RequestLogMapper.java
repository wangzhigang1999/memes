package com.memes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memes.model.pojo.RequestLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RequestLogMapper extends BaseMapper<RequestLog> {
}

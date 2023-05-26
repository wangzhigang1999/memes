package com.bupt.dailyhaha.mapper.sqlite;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bupt.dailyhaha.pojo.common.LogDocument;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MLog extends BaseMapper<LogDocument> {
}

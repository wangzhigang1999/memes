package com.bupt.dailyhaha.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bupt.dailyhaha.pojo.common.LogDocument;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MLog extends BaseMapper<LogDocument> {
}

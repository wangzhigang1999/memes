package com.memes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memes.model.pojo.Config;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}

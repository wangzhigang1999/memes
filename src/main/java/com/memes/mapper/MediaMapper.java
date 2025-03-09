package com.memes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memes.model.pojo.MediaContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MediaMapper extends BaseMapper<MediaContent> {
} 
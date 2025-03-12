package com.memes.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memes.model.pojo.MediaContent;

@Mapper
public interface MediaMapper extends BaseMapper<MediaContent> {
}

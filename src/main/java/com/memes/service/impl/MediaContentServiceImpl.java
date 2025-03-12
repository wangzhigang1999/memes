package com.memes.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.mapper.MediaMapper;
import com.memes.model.pojo.MediaContent;
import com.memes.service.MediaContentService;

@Service
public class MediaContentServiceImpl extends ServiceImpl<MediaMapper, MediaContent> implements MediaContentService {
}

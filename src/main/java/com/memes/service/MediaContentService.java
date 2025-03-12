package com.memes.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memes.model.pojo.MediaContent;

public interface MediaContentService extends IService<MediaContent> {

    List<MediaContent> listPendingMediaContent(Integer limit);

    boolean markMediaStatus(Integer id, MediaContent.ContentStatus status);

    int batchMarkMediaStatus(List<Integer> ids, MediaContent.ContentStatus status);

    long getNumByStatusAndDate(MediaContent.ContentStatus status, String date);
}

package com.memes.service;

import java.io.InputStream;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memes.model.pojo.MediaContent;

public interface MediaContentService extends IService<MediaContent> {

    List<MediaContent> listPendingMediaContent(Integer limit);

    List<MediaContent> listNoSharpReviewMediaContent(Integer limit);

    boolean markMediaStatus(Long id, MediaContent.ContentStatus status);

    int batchMarkMediaStatus(List<Long> ids, MediaContent.ContentStatus status);

    long getNumByStatusAndDate(MediaContent.ContentStatus status, String date);

    MediaContent storeTextFormatSubmission(String text, String mime);

    MediaContent storeStreamSubmission(InputStream inputStream, String mime);
}

package com.memes.service;

import java.util.List;

import com.memes.model.pojo.MediaContent;
import com.memes.model.pojo.MediaContent.ContentStatus;
import com.memes.model.pojo.Submission;

public interface ReviewService {

    List<MediaContent> listPendingMediaContent(Integer limit);

    boolean markMediaStatus(Integer id, ContentStatus status);

    int batchMarkMediaStatus(List<Integer> ids, ContentStatus status);

    long getNumByStatusAndDate(ContentStatus status, String date);

    Submission mergeTwoSubmission(Integer first, Integer second);

}

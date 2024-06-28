package com.memes.service.review;

import com.memes.model.submission.Submission;

import java.util.List;

public interface ReviewService {

    List<Submission> listWaitingSubmission(Integer limit);

    boolean reviewSubmission(String id, String operation);

    int batchReviewSubmission(List<String> ids, String operation);

    long getStatusNum(String status);

}

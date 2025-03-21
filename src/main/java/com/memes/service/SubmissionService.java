package com.memes.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memes.model.pojo.Submission;

public interface SubmissionService extends IService<Submission> {

    Submission mergeTwoSubmission(Long first, Long second);

    Submission updateSubmissionCount(Long id, boolean isLike);

    List<Submission> list(Integer pageSize, Long lastId, String date);
}

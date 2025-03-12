package com.memes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memes.model.pojo.Submission;

public interface SubmissionService extends IService<Submission> {
    public Submission mergeTwoSubmission(Integer first, Integer second);

}

package com.memes.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.mapper.SubmissionMapper;
import com.memes.model.pojo.Submission;
import com.memes.service.SubmissionService;

@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {
}

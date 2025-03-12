package com.memes.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.mapper.SubmissionMapper;
import com.memes.model.pojo.Submission;
import com.memes.service.SubmissionService;

@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {
    private final SubmissionMapper submissionMapper;

    public SubmissionServiceImpl(SubmissionMapper submissionMapper) {
        this.submissionMapper = submissionMapper;
    }

    @Override
    public Submission mergeTwoSubmission(Integer first, Integer second) {
        Submission firstSub = submissionMapper.selectById(first);
        Submission secondSub = submissionMapper.selectById(second);
        if (firstSub != null && secondSub != null) {
            firstSub.getMediaContentIdList().addAll(secondSub.getMediaContentIdList());
            firstSub.getMediaContentIdList().sort(Integer::compareTo);
            firstSub.getTags().addAll(secondSub.getTags());
            submissionMapper.updateById(firstSub);
            submissionMapper.deleteById(second);
            return firstSub;
        }
        return null;
    }
}

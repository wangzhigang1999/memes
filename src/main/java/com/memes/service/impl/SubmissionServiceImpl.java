package com.memes.service.impl;

import static com.google.common.collect.Sets.union;

import java.util.Collections;
import java.util.Set;

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

            Set<String> firstTags = firstSub.getTags() != null ? firstSub.getTags() : Collections.emptySet();
            Set<String> secondTags = secondSub.getTags() != null ? secondSub.getTags() : Collections.emptySet();
            Set<String> mergedTags = union(firstTags, secondTags);
            firstSub.setTags(mergedTags);
            submissionMapper.updateById(firstSub);
            submissionMapper.deleteById(second);
            return firstSub;
        }
        return null;
    }
}

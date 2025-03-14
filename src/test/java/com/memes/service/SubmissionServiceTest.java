package com.memes.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.memes.mapper.SubmissionMapper;
import com.memes.model.pojo.Submission;

@SpringBootTest
class SubmissionServiceTest {

    @Autowired
    SubmissionService submissionService;

    @Autowired
    SubmissionMapper submissionMapper;

    @Test
    void mergeTwoSubmission() {

        submissionMapper.delete(null);

        // insert two submissions
        int insert = submissionMapper.insert(Submission.builder().id(114).mediaContentIdList(List.of(1, 2)).build());
        assert insert > 0;

        insert = submissionMapper.insert(Submission.builder().id(514).mediaContentIdList(List.of(3, 4)).build());
        assert insert > 0;

        Submission mergeTwoSubmission = submissionService.mergeTwoSubmission(114, 514);
        assert mergeTwoSubmission != null;
        assert mergeTwoSubmission.getMediaContentIdList().size() == 4;
    }
}

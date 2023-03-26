package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.submission.Submission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SubmissionServiceTest {

    @Autowired
    SubmissionService submissionService;

    @Test
    void deleteByName() {
        boolean delete = submissionService.deleteByName("test");
        assert !delete;
    }

    @Test
    void getToday() {
        List<Submission> today = submissionService.getTodaySubmissions();
        assert today.size() > 0;
    }

    @Test
    void vote() {
        assert submissionService.vote("25c346da-7e21-4365-8474-089b3cd8e5ec.jpg", true);
        assert submissionService.vote("25c346da-7e21-4365-8474-089b3cd8e5ec.jpg", false);

    }

    @Test
    void getLastHistory() {
        List<Submission> lastHistory = submissionService.getLastHistory();
        assert lastHistory.size() > 0;
    }

    @Test
    void updateHistory() {
        Submission submission = new Submission();
        submission.setName("test");
        submission.setUrl("test");
        submission.setTimestamp(System.currentTimeMillis());
        submission.setDeleted(false);
        submission.setUp(0);
        submission.setDown(0);

        boolean history = submissionService.updateHistory("2021-05-01", List.of(submission));
        assert history;
    }

    @Test
    void getHistory() {
        List<Submission> history = submissionService.getHistory("2021-05-01");
        assert history.size() > 0;
    }
}
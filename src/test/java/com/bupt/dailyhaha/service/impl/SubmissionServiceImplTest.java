package com.bupt.dailyhaha.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SubmissionServiceImplTest {

    @Autowired
    SubmissionServiceImpl submissionService;
    @Test
    void getHistoryDates() {
        List<String> historyDates = submissionService.getHistoryDates(10);
        System.out.println(historyDates);
    }
}
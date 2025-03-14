package com.memes.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.memes.mapper.RequestLogMapper;
import com.memes.model.pojo.RequestLog;
import com.memes.model.response.VisitStatistic;
import com.memes.util.RequestLogTestUtil;
import com.memes.util.TimeUtil;

@SpringBootTest
class AdminServiceTest {
    @Autowired
    AdminService adminService;

    @Autowired
    RequestLogMapper requestLogMapper;

    @Test
    void getVisitStatistic() {

        List<RequestLog> requestLogs = RequestLogTestUtil.generateRequestLogs(100);
        for (RequestLog requestLog : requestLogs) {
            requestLogMapper.insert(requestLog);
        }

        VisitStatistic visitStatistic = adminService.getVisitStatistic(TimeUtil.getYMD());
        assertNotNull(visitStatistic);

    }

    @Test
    void getReviewStatistic() {
        Map<String, Long> reviewStatistic = adminService.getReviewStatistic();
        assertNotNull(reviewStatistic);
    }
}

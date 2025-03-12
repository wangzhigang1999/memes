package com.memes.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.memes.model.response.VisitStatistic;
import com.memes.util.GsonUtil;
import com.memes.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class AdminServiceTest {

    @Autowired
    AdminService adminService;

    @Test
    void getVisitStatistic() {
        VisitStatistic visitStatistic = adminService.getVisitStatistic(TimeUtil.getYMD());
        assertNotNull(visitStatistic);
        log.info(GsonUtil.toPrettyJson(visitStatistic));
    }
}

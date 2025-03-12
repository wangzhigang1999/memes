package com.memes.service.impl;

import com.memes.model.response.VisitStatistic;
import com.memes.util.GsonUtil;
import com.memes.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

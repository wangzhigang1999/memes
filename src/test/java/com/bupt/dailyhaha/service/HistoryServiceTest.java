package com.bupt.dailyhaha.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HistoryServiceTest {

    @Autowired
    HistoryService service;

    @Test
    void getHistory() {
        assert service.getHistory("2023-04-05").size() > 0;
    }

    @Test
    void getHistoryDates() {
        List<String> historyDates = service.getHistoryDates(10);
        assert historyDates.size() > 0;
    }

}
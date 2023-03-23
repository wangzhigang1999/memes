package com.bupt.dailyhaha.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImageOpsTest {

    @Autowired
    ImageOps imageOps;
    @Test
    void getLastHistory() {
        System.out.println(imageOps.getLastHistory());
    }
}
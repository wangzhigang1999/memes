package com.bupt.dailyhaha;

import com.bupt.dailyhaha.service.ImageOps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImageOpsTest {

    @Autowired
    ImageOps ops;

    @Test
    void deleteByName() {
    }

    @Test
    void getToday() {
        System.out.println(ops.getToday());
    }
}
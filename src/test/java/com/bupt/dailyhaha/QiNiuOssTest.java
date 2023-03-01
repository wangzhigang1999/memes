package com.bupt.dailyhaha;

import com.qiniu.common.QiniuException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QiNiuOssTest {

    @Autowired
    QiNiuOss oss;

    @Test
    void putImg() throws QiniuException {
        System.out.println(oss.putImg("D:\\codes\\dailyHaha\\FoE6an2XgAI5_dA.jpg"));
    }
}
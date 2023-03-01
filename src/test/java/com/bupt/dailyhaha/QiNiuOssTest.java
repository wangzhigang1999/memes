package com.bupt.dailyhaha;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
class QiNiuOssTest {

    @Autowired
    QiNiuOss oss;

    @Test
    void putImg() {
    }

    @Test
    void imageTypeCheck() throws IOException {
        File img = new File("example.jpg");
        MockMultipartFile file;
        try (FileInputStream stream = new FileInputStream(img)) {
            file = new MockMultipartFile("file", "example.jpg", "image/jpeg", stream.readAllBytes());
        }
        System.out.println(QiNiuOss.imageTypeCheck(file.getInputStream()));
    }

}
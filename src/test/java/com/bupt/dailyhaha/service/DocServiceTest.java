package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.PageResult;
import com.bupt.dailyhaha.pojo.doc.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DocServiceTest {

    @Autowired
    DocService docService;

    @Test
    void getDocs() {
        PageResult<Document> docs = docService.getDocs("6461e186675de90cbebdc1d0", 2, 2, false);
        System.out.println(docs);
        for (Document document : docs.getList()) {
            System.out.println(document.getTitle());
        }
    }
}
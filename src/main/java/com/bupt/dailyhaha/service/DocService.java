package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.doc.Document;

import java.util.List;

public interface DocService {
    Document getDoc(String id);

    List<Document> getDocs();

    Document create(Document doc);

    Document update(Document doc);


}

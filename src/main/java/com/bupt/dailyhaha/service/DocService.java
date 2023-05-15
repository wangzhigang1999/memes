package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.doc.Document;

public interface DocService {
    Document getDoc(String id);

    Document create(Document doc);

    Document update(Document doc);

    PageResult<Document> getDocs(String lastID, Integer pageSize, Integer pageNum,boolean containPrivate);


    boolean setPrivate(String docID, boolean isPrivate);

    boolean delete(String docID);
}

package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.doc.Document;
import com.bupt.dailyhaha.service.DocService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocServiceImpl implements DocService {

    final MongoTemplate mongoTemplate;

    public DocServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public Document getDoc(String id) {
        return mongoTemplate.findById(id, Document.class);
    }

    @Override
    public List<Document> getDocs() {
        return mongoTemplate.findAll(Document.class);
    }

    @Override
    public Document create(Document doc) {
        doc.setCreateTime(System.currentTimeMillis());
        if (doc.getId() != null) {
            return update(doc);
        }
        return mongoTemplate.save(doc);
    }

    @Override
    public Document update(Document doc) {
        if (doc.getId() == null) {
            return null;
        }
        doc.setUpdateTime(System.currentTimeMillis());
        return mongoTemplate.save(doc);

    }
}

package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.doc.Document;
import com.bupt.dailyhaha.service.DocService;
import com.bupt.dailyhaha.service.MongoPageHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class DocServiceImpl implements DocService {

    final MongoTemplate mongoTemplate;

    final MongoPageHelper mongoPageHelper;

    Logger logger = LoggerFactory.getLogger(DocServiceImpl.class);

    static Cache<String, Document> documentCache = CacheBuilder.newBuilder().maximumSize(100).build();


    public DocServiceImpl(MongoTemplate mongoTemplate, MongoPageHelper mongoPageHelper) {
        this.mongoTemplate = mongoTemplate;
        this.mongoPageHelper = mongoPageHelper;
    }


    @Override
    public Document getDoc(String id) {
        Document document = documentCache.getIfPresent(id);
        if (document != null) {
            return document;
        }
        document = mongoTemplate.findById(id, Document.class);
        if (document != null) {
            documentCache.put(id, document);
        }
        return document;
    }


    @Override
    public Document create(Document doc) {
        doc.setCreateTime(System.currentTimeMillis());
        if (doc.getId() != null) {
            return update(doc);
        }
        Document document = mongoTemplate.save(doc);
        documentCache.put(document.getId(), document);
        return document;
    }

    @Override
    public Document update(Document doc) {
        if (doc.getId() == null) {
            return null;
        }
        doc.setUpdateTime(System.currentTimeMillis());
        Document document = mongoTemplate.save(doc);
        documentCache.put(doc.getId(), document);
        return document;
    }

    @Override
    public PageResult<Document> getDocs(String lastID, Integer pageSize, Integer pageNum, boolean containPrivate) {
        logger.info("lastID: {}, pageSize: {}, pageNum: {}", lastID, pageSize, pageNum);
        Query query = new Query();
        if (!containPrivate) {
            query.addCriteria(Criteria.where("privateDoc").is(false));
        }
        return mongoPageHelper.pageQuery(query, Document.class, pageSize, pageNum, lastID);
    }

    @Override
    public boolean setPrivate(String docID, boolean isPrivate) {
        documentCache.invalidate(docID);
        Document doc = mongoTemplate.findById(docID, Document.class);
        if (doc == null) {
            return false;
        }
        doc.setPrivateDoc(isPrivate);
        Document document = mongoTemplate.save(doc);
        return document.isPrivateDoc() == isPrivate;
    }

    @Override
    public boolean delete(String docID) {
        documentCache.invalidate(docID);
        Document doc = mongoTemplate.findById(docID, Document.class);
        if (doc == null) {
            return false;
        }
        doc.setDeleted(true);
        Document document = mongoTemplate.save(doc);
        return document.isDeleted();
    }

}

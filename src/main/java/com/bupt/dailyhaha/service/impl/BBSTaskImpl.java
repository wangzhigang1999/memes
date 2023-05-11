package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.BBSRecord;
import com.bupt.dailyhaha.service.BBSTask;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BBSTaskImpl implements BBSTask {

    final MongoTemplate mongoTemplate;

    public BBSTaskImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean create(BBSRecord.Post post) {

        // check if the post is already in the database
        Query query = new Query();
        query.addCriteria(Criteria.where("post.id").is(post.getId()));
        if (mongoTemplate.exists(query, BBSRecord.class)) {
            return false;
        }
        BBSRecord bbsRecord = new BBSRecord(post);
        BBSRecord insert = mongoTemplate.insert(bbsRecord);
        return insert.getId() != null && insert.getId().length() > 0;
    }

    @Override
    public List<BBSRecord> getTasks(BBSRecord.Status status) {
        // find the unfinished tasks
        Query query = new Query();
        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        return mongoTemplate.find(query, BBSRecord.class);
    }

    @Override
    public boolean setStatus(String id, BBSRecord.Status status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        BBSRecord bbsRecord = mongoTemplate.findOne(query, BBSRecord.class);
        if (bbsRecord == null) {
            return false;
        }
        bbsRecord.setStatus(status);
        mongoTemplate.save(bbsRecord);
        return true;
    }
}

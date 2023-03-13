package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.Image;
import com.bupt.dailyhaha.service.ImageOps;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ImageOpsImpl implements ImageOps {
    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public void deleteByName(String name) {
        var query = new Query(Criteria.where("name").is(name));
        UpdateResult deleted = mongoTemplate.update(Image.class).matching(query).apply(new Update().set("deleted", true)).first();
        deleted.getModifiedCount();
    }

    @Override
    public List<Image> getToday() {
        // from 00:00:00 of today
        var from = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(9, ChronoUnit.HOURS);

        // deleted = false
        return mongoTemplate.find(Query.query(Criteria.where("time").gte(from).and("deleted").ne(true)), Image.class);
    }
}

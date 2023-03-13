package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.Image;
import com.bupt.dailyhaha.service.ImageOps;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ImageOpsImpl implements ImageOps {
    @Resource
    MongoTemplate mongoTemplate;
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(ImageOpsImpl.class);


    BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public ImageOpsImpl() {
        new Thread(() -> {
            while (true) {
                try {
                    String name = queue.take();
                    boolean delete = deleteByName(name);
                    if (!delete) {
                        queue.put(name);
                        logger.info("delete task failed, retry");
                    }
                    logger.info("delete task finished");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.error("delete task interrupted");
                }
            }
        }).start();
    }


    @Override
    public boolean deleteByName(String name) {
        var query = new Query(Criteria.where("name").is(name));
        mongoTemplate.update(Image.class).matching(query).apply(new Update().set("deleted", true)).first();
        Image one = mongoTemplate.findOne(query, Image.class);
        return one != null && one.getDeleted();
    }


    @Override
    public List<Image> getToday() {
        // from 00:00:00 of today
        var from = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(9, ChronoUnit.HOURS);

        // deleted = false
        return mongoTemplate.find(Query.query(Criteria.where("time").gte(from).and("deleted").ne(true)), Image.class);
    }

    @Override
    public void addDeleteTask(String name) {
        queue.add(name);
    }
}

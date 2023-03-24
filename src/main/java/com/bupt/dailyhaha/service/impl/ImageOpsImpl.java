package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.History;
import com.bupt.dailyhaha.pojo.Image;
import com.bupt.dailyhaha.service.ImageOps;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时
        var from = start - 2 * 60 * 60 * 1000;

        // 向后推22个小时
        var to = start + 22 * 60 * 60 * 1000;
        return mongoTemplate.find(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("deleted").ne(true)), Image.class);
    }

    @Override
    public void addDeleteTask(String name) {
        queue.add(name);
    }

    @Override
    public boolean vote(String name, boolean up) {
        // if up is true, then vote up, else vote down
        var query = new Query(Criteria.where("name").is(name));
        var update = new Update();
        if (up) {
            update.inc("up", 1);
        } else {
            update.inc("down", 1);
        }
        UpdateResult first = mongoTemplate.update(Image.class).matching(query).apply(update).first();
        return first.getMatchedCount() > 0;
    }

    @Override
    public List<Image> getLastHistory() {
        // 查询history表中最后一条记录
        History history = mongoTemplate.findOne(
                Query.query(Criteria.where("timestamp").exists(true))
                        .limit(1)
                        .with(Sort.by(Sort.Direction.DESC, "timestamp")), History.class);
        return history == null ? new ArrayList<>() : history.getImages();
    }

    @Override
    public boolean updateHistory(String date, List<Image> images) {
        History history = new History();
        history.setDate(date);
        history.setImages(images);
        history.setTimestamp(System.currentTimeMillis());


        Update update = new Update().set("images", images).set("timestamp", System.currentTimeMillis()).set("count", images.size());
        UpdateResult result = mongoTemplate.upsert(Query.query(Criteria.where("date").is(date)), update, History.class);
        return result.getUpsertedId() != null || result.getModifiedCount() > 0;
    }
}

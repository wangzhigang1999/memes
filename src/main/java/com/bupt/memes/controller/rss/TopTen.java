package com.bupt.memes.controller.rss;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/topTen")
@CrossOrigin(origins = "*")
public class TopTen {
    final MongoTemplate mongoTemplate;

    public TopTen(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping()
    public com.bupt.memes.model.TopTen GetTopTen() {
        // find last
        return mongoTemplate.findOne(new Query().limit(1).with(Sort.by(Sort.Direction.DESC, "_id")), com.bupt.memes.model.TopTen.class);
    }
}

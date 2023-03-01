package com.bupt.dailyhaha;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

@SpringBootTest
class ControllerTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void today() {
        mongoTemplate.findAll(Image.class).forEach(System.out::println);

        // time range

        var template = "[md] ![%d](%s) [/md]";

        // now to 24h before
        Date now = Date.from(java.time.Instant.now());
        Date before = Date.from(now.toInstant().minusSeconds(24 * 60 * 60));
        List<Image> time = mongoTemplate.find(Query.query(Criteria.where("time").gte(before).lte(now)), Image.class);

        for (Image image : time) {
            System.out.printf((template) + "%n", image.getHash(), image.getUrl());
        }
    }
}
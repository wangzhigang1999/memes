package com.bupt.dailyhaha;

import com.bupt.dailyhaha.pojo.submission.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;

@SpringBootTest
class MemesTest {

    @Autowired
    MongoTemplate template;

    @Test
    void main() {
        Date date = new Date();
        System.out.println(date);
        System.out.println(date.getTime());
        System.out.println(date.toInstant());

        // convert date to 00:00:00 of today
        date = Date.from(date.toInstant().truncatedTo(java.time.temporal.ChronoUnit.DAYS).minus(8, java.time.temporal.ChronoUnit.HOURS));
        System.out.println(date);

        template.find(Query.query(Criteria.where("time").gte(date)), Image.class)
                .forEach(System.out::println);
    }
}
package com.bupt.dailyhaha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/img")
public class Controller {
    final Storage storage;
    final MongoTemplate mongoTemplate;


    @Value("${token}")
    String localToken = UUID.randomUUID().toString();


    public Controller(Storage storage, MongoTemplate mongoTemplate) {

        System.out.println(storage.getClass().getCanonicalName());
        this.storage = storage;
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping("/upload")
    public Object upload(MultipartFile file, boolean personal) throws IOException {
        return storage.store(file.getInputStream(), personal);
    }

    @GetMapping("/today")
    public Object today(String token) {
        // 做鉴权
        if (!localToken.equals(token)) {
            return List.of("https://oss-bbs.bupt.site/example.jpg");
        }
        var template = "[md] ![%d](%s) [/md]";
        List<String> ans = new ArrayList<>();
        // from 00:00:00 to 23:59:59
        Date from = Date.from(Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS));
        Date to = Date.from(Instant.now());
        List<Image> time = mongoTemplate.find(Query.query(Criteria.where("time").gte(from).lte(to)), Image.class);
        for (Image image : time) {
            ans.add(String.format(template, image.getHash(), image.getUrl()));
        }
        return ans;
    }

}

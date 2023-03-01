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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/img")
public class Controller {
    final QiNiuOss qiNiuOss;
    final MongoTemplate mongoTemplate;

    @Value("${token}")
    String localToken = UUID.randomUUID().toString();


    public Controller(QiNiuOss qiNiuOss, MongoTemplate mongoTemplate) {
        this.qiNiuOss = qiNiuOss;
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping("/upload")
    public Object upload(MultipartFile file, boolean personal) throws IOException {
        return qiNiuOss.putImg(file.getInputStream(), personal);
    }

    @GetMapping("/today")
    public Object today(String token) {
        if (!localToken.equals(token)) {
            return List.of("???");
        }

        var template = "[md] ![%d](%s) [/md]";

        List<String> ans = new ArrayList<>();

        // now to 24h before
        Date now = Date.from(java.time.Instant.now());
        Date before = Date.from(now.toInstant().minusSeconds(24 * 60 * 60));
        List<Image> time = mongoTemplate.find(Query.query(Criteria.where("time").gte(before).lte(now)), Image.class);

        for (Image image : time) {
            ans.add(String.format(template, image.getHash(), image.getUrl()));
        }
        return ans;
    }


}

package com.bupt.dailyhaha;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/img")
public class Controller {
    final QiNiuOss qiNiuOss;
    final MongoTemplate mongoTemplate;


    public Controller(QiNiuOss qiNiuOss, MongoTemplate mongoTemplate) {
        this.qiNiuOss = qiNiuOss;
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping("/upload")
    public Object upload(MultipartFile file, boolean personal) throws IOException {
        String img = qiNiuOss.putImg(file.getBytes());
        Image image = new Image();
        image.setUrl(img);
        image.setTime(Date.from(java.time.Instant.now()));
        if (!personal) {
            mongoTemplate.save(image);
        }
        return image;
    }

    @GetMapping("/today")
    public Object today() {

        // now to 24h before
        Date now = Date.from(java.time.Instant.now());
        Date before = Date.from(now.toInstant().minusSeconds(24 * 60 * 60));

        return mongoTemplate.find(Query.query(Criteria.where("time").gte(before).lte(now)), Image.class);
    }


}

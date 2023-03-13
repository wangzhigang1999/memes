package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.pojo.Image;
import com.bupt.dailyhaha.pojo.ImageReviewCallback;
import com.bupt.dailyhaha.service.ImageOps;
import com.bupt.dailyhaha.service.Storage;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static com.bupt.dailyhaha.Audit.start;

@RestController
public class Controller {
    final Storage storage;
    final MongoTemplate mongoTemplate;
    final MongoClient client;
    final ImageOps imageOps;
    final static Gson gson = new Gson();


    @Value("${token}")
    String localToken = UUID.randomUUID().toString();


    public Controller(Storage storage, MongoTemplate mongoTemplate, MongoClient client, ImageOps imageOps) {
        this.storage = storage;
        this.mongoTemplate = mongoTemplate;
        this.client = client;
        this.imageOps = imageOps;
    }

    @RequestMapping("/img/upload")
    public Image upload(MultipartFile file, boolean personal) throws IOException {
        if (file == null || file.isEmpty()) {
            return new Image("---似乎发生了一些错误---", new Date(0), 0, "", false);
        }
        return storage.store(file.getInputStream(), personal);
    }

    @GetMapping("/img/today")
    public Object today(String token) {
        // 做鉴权
        if (!localToken.equals(token)) {
            return List.of("https://oss-bbs.bupt.site/example.jpg");
        }
        var template = "[md] ![%d](%s) [/md]";
        List<String> ans = new ArrayList<>();
        List<Image> images = imageOps.getToday();
        for (Image image : images) {
            ans.add(String.format(template, image.getHash(), image.getUrl()));
        }
        return ans;
    }

    @GetMapping("/up")
    public Object up(String token) {
        // 做鉴权
        if (!localToken.equals(token)) {
            return Map.of("status", "error", "msg", "token error");
        }
        long duration = System.currentTimeMillis() - start;

        // convert start to yyyy-MM-dd HH:mm:ss with beijing time zone
        var str = Instant.ofEpochMilli(start).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();

        //convert duration to hours
        var hours = duration / 1000 / 60 / 60;

        if (hours > 24) {
            return Map.of("status", "ok", "msg", "up " + hours / 24.0 + " days", "start at:", str);
        } else if (hours > 1) {
            return Map.of("status", "ok", "msg", "up " + hours + " hours", "start at:", str);
        }

        // convert duration to minutes
        var minutes = duration / 1000 / 60;
        if (minutes > 1) {
            return Map.of("status", "ok", "msg", "up " + minutes + " minutes", "start at:", str);
        } else {
            return Map.of("status", "ok", "msg", "up " + duration / 1000 + " seconds", "start at:", str);
        }
    }

    @RequestMapping("/img/reviewCallback")
    public Object reviewCallback(@RequestBody Map<String, Object> map) {
        ImageReviewCallback callback = gson.fromJson(gson.toJson(map), ImageReviewCallback.class);

        // write to mongodb
        client.getDatabase("shadiao").getCollection("ReviewCallback").insertOne(new Document(map));

        // check if the image is disabled
        boolean disable = callback.getItems().get(0).getResult().isDisable();

        if (disable) {
            imageOps.addDeleteTask(callback.getInputKey());
        }
        return up(this.localToken);
    }
}

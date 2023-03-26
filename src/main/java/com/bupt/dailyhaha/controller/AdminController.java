package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.submission.Image;
import com.bupt.dailyhaha.pojo.ImageReviewCallback;
import com.bupt.dailyhaha.service.ImageOps;
import com.bupt.dailyhaha.service.Storage;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.bupt.dailyhaha.aspect.Audit.start;

@RestController
public class AdminController {

    final Storage storage;
    final MongoTemplate mongoTemplate;
    final MongoClient client;
    final ImageOps imageOps;
    final static Gson gson = new Gson();


    @Value("${spring.data.mongodb.database}")
    String database;
    @Value("${token}")
    String localToken = UUID.randomUUID().toString();

    public AdminController(Storage storage, MongoTemplate mongoTemplate, MongoClient client, ImageOps imageOps) {
        this.storage = storage;
        this.mongoTemplate = mongoTemplate;
        this.client = client;
        this.imageOps = imageOps;
    }

    /**
     * 删除图片
     *
     * @param token 鉴权
     * @param name  图片名
     * @return 删除结果
     */
    @PostMapping("/img/delete")
    public Object delete(String token, String name) {
        // 做鉴权
        if (!localToken.equals(token)) {
            return Map.of("status", "error", "msg", "token error");
        }
        boolean delete = imageOps.deleteByName(name);
        return Map.of("status", "ok", "msg", delete ? "delete success" : "delete failed");
    }

    /**
     * 获取今日图片，实时的，用于管理员来审核
     *
     * @param token 鉴权
     * @return 今日图片
     */
    @GetMapping("/img/today")
    public Object today(String token) {
        if (!localToken.equals(token)) {
            return Map.of("status", "error", "msg", "token error");
        }
        return imageOps.getToday();
    }

    /**
     * 发布今日图片，将今日图片转移到历史图片中
     *
     * @param token 鉴权
     * @return 发布结果
     */
    @RequestMapping("/img/release")
    public Object release(String token) {
        if (!localToken.equals(token)) {
            return Map.of("status", "error", "msg", "token error");
        }
        List<Image> today = imageOps.getToday();
        boolean history = imageOps.updateHistory(Utils.getYMD(), today);
        return Map.of("status", "ok", "msg", history ? "release success" : "release failed");
    }


    @GetMapping("/up")
    public Object up(String token) {
        if (!localToken.equals(token)) {
            return Map.of("status", "error", "msg", "token error");
        }
        long duration = System.currentTimeMillis() - start;
        return Utils.up(duration);
    }

    /**
     * 七牛的审核回调
     *
     * @param map 回调的数据
     * @return 处理结果
     */
    @RequestMapping("/img/reviewCallback")
    public Object reviewCallback(@RequestBody Map<String, Object> map) {
        ImageReviewCallback callback = gson.fromJson(gson.toJson(map), ImageReviewCallback.class);
        // write to mongodb
        client.getDatabase(database).getCollection("ReviewCallback").insertOne(new Document(map));
        // code!= 0 means review failed
        if (callback == null || callback.getCode() != 0) {
            return up(this.localToken);
        }
        // check if the image is disabled
        boolean disable = callback.getItems().get(0).getResult().isDisable();

        if (disable) {
            imageOps.addDeleteTask(callback.getInputKey());
        }
        return up(this.localToken);
    }
}

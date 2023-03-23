package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.pojo.Image;
import com.bupt.dailyhaha.service.ImageOps;
import com.bupt.dailyhaha.service.Storage;
import com.mongodb.client.MongoClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@org.springframework.web.bind.annotation.RestController
public class UserController {
    final Storage storage;
    final MongoClient client;
    final ImageOps imageOps;


    public UserController(Storage storage, MongoClient client, ImageOps imageOps) {
        this.storage = storage;
        this.client = client;
        this.imageOps = imageOps;
    }

    /**
     * 上传图片
     *
     * @param file     图片文件
     * @param personal 是否为个人图片
     * @param uuid     用户uuid
     * @return 图片信息
     * @throws IOException 上传失败
     */
    @RequestMapping("/img/upload")
    public Image upload(MultipartFile file, boolean personal, String uuid) throws IOException {
        assert uuid != null;
        if (file == null || file.isEmpty()) {
            return new Image("---似乎发生了一些错误---", new Date(0), 0, "", false, System.currentTimeMillis());
        }
        return storage.store(file.getInputStream(), personal);
    }

    /**
     * 获取投稿信息,供前端调用，可以保证图片都是审核过的
     *
     * @param uuid 用户uuid
     * @return 投稿信息
     */

    @GetMapping("/img/content")
    public Object content(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return Map.of("status", "error", "msg", "uuid error");
        }
        return imageOps.getLastHistory();
    }


    /**
     * 👍 or 👎
     *
     * @param uuid 用户uuid
     * @param name 图片名
     * @param up   是否赞
     * @return 状态
     */
    @RequestMapping("/img/vote")
    public Object vote(String uuid, String name, boolean up) {
        if (uuid == null || uuid.isEmpty()) {
            return Map.of("status", "error", "msg", "uuid error");
        }
        boolean vote = imageOps.vote(name, up);
        return Map.of("status", "ok", "msg", vote ? "vote success" : "vote failed", "code", vote ? 0 : 1);
    }
}

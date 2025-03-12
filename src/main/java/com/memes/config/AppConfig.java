package com.memes.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.memes.annotation.DynamicConfig;
import com.memes.model.pojo.Config;
import com.memes.model.pojo.Submission;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用配置，动态更新 简单的配置条目使用注解标记字段，复杂的配置条目使用注解标记方法
 */
@Slf4j
@Component("appConfig")
@Data
public class AppConfig {
    @DynamicConfig(key = "bot.up", desc = "机器人是否开启", defaultValue = "true", type = Config.Type.BOOLEAN)
    public boolean botUp;
    @DynamicConfig(key = "submission.num.min", desc = "每天的最少投稿数", defaultValue = "50")
    public int minSubmissions = 50;
    @DynamicConfig(key = "submission.fetch.limit", desc = "每次获取的最大投稿数", defaultValue = "20")
    public int subFetchLimit = 20;
    @DynamicConfig(key = "server.down", desc = "服务器是否停止服务", defaultValue = "false", type = Config.Type.BOOLEAN)
    public boolean serverDown = false;

    public Set<Submission> topSubmissions = new CopyOnWriteArraySet<>();

    @DynamicConfig(key = "top.submission", desc = "置顶的投稿", defaultValue = "[]", type = Config.Type.JSON, visible = false)
    public void setTopSubmissions(String topSubmissions) {
        if (topSubmissions == null || topSubmissions.isEmpty()) {
            return;
        }
        Submission[] submissions = new Gson().fromJson(topSubmissions, Submission[].class);
        this.topSubmissions = new HashSet<>(Arrays.asList(submissions));
    }

    public Set<String> uidBlacklist = new CopyOnWriteArraySet<>();

    @DynamicConfig(key = "blacklist", desc = "黑名单", defaultValue = "[]", type = Config.Type.JSON, visible = false)
    public void setUidBlacklist(String uidBlacklist) {
        if (uidBlacklist == null || uidBlacklist.isEmpty()) {
            return;
        }
        String[] uuids = new Gson().fromJson(uidBlacklist, String[].class);
        this.uidBlacklist.clear();
        this.uidBlacklist.addAll(Arrays.asList(uuids));
    }
}

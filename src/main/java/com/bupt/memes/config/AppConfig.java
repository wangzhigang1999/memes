package com.bupt.memes.config;

import com.bupt.memes.anno.DynamicConfig;
import com.bupt.memes.model.media.Submission;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class AppConfig {
    @DynamicConfig(key = "bot.up", desc = "机器人是否开启", defaultValue = "true")
    public boolean botUp;
    @DynamicConfig(key = "submission.num.min", desc = "每天的最少投稿数", defaultValue = "50")
    public int minSubmissions = 50;
    @DynamicConfig(key = "topk", desc = "相似检索时的最大返回数", defaultValue = "10")
    public int topK = 10;
    @DynamicConfig(key = "cache.size", desc = "缓存的大小", defaultValue = "1000")
    public int submissionCacheSize = 1000;
    @DynamicConfig(key = "index.version", desc = "索引的版本", defaultValue = "0")
    public long indexVersion = 0;
    @DynamicConfig(key = "index.file", desc = "索引的加载位置", defaultValue = "hnsw.index")
    public String indexFile = "hnsw.index";

    public Set<Submission> topSubmissions = new HashSet<>();

    @DynamicConfig(key = "top.submission", desc = "置顶的投稿", defaultValue = "[]")
    public void setTopSubmissions(String topSubmissions) {
        if (topSubmissions == null || topSubmissions.isEmpty()) {
            return;
        }
        Submission[] submissions = new Gson().fromJson(topSubmissions, Submission[].class);
        this.topSubmissions = new HashSet<>(Arrays.asList(submissions));
    }
}

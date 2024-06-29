package com.memes.config;

import com.google.gson.Gson;
import com.memes.annotation.DynamicConfig;
import com.memes.exception.AppException;
import com.memes.model.ConfigItem;
import com.memes.model.submission.Submission;
import com.memes.util.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 应用配置，动态更新
 * 简单的配置条目使用注解标记字段，复杂的配置条目使用注解标记方法
 */
@Slf4j
@Component("appConfig")
public class AppConfig {
    @DynamicConfig(key = "bot.up", desc = "机器人是否开启", defaultValue = "true", type = ConfigItem.Type.BOOLEAN)
    public boolean botUp;
    @DynamicConfig(key = "submission.num.min", desc = "每天的最少投稿数", defaultValue = "50")
    public int minSubmissions = 50;
    @DynamicConfig(key = "topk", desc = "相似检索时的最大返回数", defaultValue = "10")
    public int topK = 10;
    @DynamicConfig(key = "cache.size", desc = "缓存的大小", defaultValue = "1000")
    public int submissionCacheSize = 1000;
    @DynamicConfig(key = "index.version", desc = "索引的版本", defaultValue = "0", visible = false)
    public long indexVersion = 0;
    @DynamicConfig(key = "index.file", desc = "索引的加载位置", defaultValue = "hnsw.index", type = ConfigItem.Type.STRING, visible = false)
    public String indexFile = "hnsw.index";
    @DynamicConfig(key = "index.persist.threshold", desc = "索引持久化的阈值", defaultValue = "50")
    public Long indexPersistThreshold = 50L;
    @DynamicConfig(key = "rss.fetch.limit", desc = "RSS 获取的最大数量", defaultValue = "20")
    public int rssFetchLimit = 20;
    @DynamicConfig(key = "news.fetch.limit", desc = "新闻获取的最大数量", defaultValue = "20")
    public int newsFetchLimit = 20;
    @DynamicConfig(key = "submission.fetch.limit", desc = "每次获取的最大投稿数", defaultValue = "20")
    public int subFetchLimit = 20;

    public Set<Submission> topSubmissions = new HashSet<>();

    @DynamicConfig(key = "top.submission", desc = "置顶的投稿", defaultValue = "[]", type = ConfigItem.Type.JSON, visible = false)
    public void setTopSubmissions(String topSubmissions) {
        if (topSubmissions == null || topSubmissions.isEmpty()) {
            return;
        }
        Submission[] submissions = new Gson().fromJson(topSubmissions, Submission[].class);
        this.topSubmissions = new HashSet<>(Arrays.asList(submissions));
    }

    final MongoTemplate mongoTemplate;

    public AppConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setBotUp(boolean botUp) {
        this.botUp = botUp;
        this.updateConfig("bot.up", String.valueOf(botUp));
    }

    public void setTopSubmissions(Set<Submission> topSubmissions) {
        this.topSubmissions = topSubmissions;
        this.updateConfig("top.submission", new Gson().toJson(topSubmissions));
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
        this.updateConfig("index.file", indexFile);
    }

    public void setIndexVersion(long indexVersion) {
        this.indexVersion = indexVersion;
        this.updateConfig("index.version", String.valueOf(indexVersion));
    }

    private void updateConfig(String key, String value) {
        Criteria criteria = Criteria.where("key").is(key);
        ConfigItem configItem = mongoTemplate.findAndModify(
                Query.query(criteria),
                new Update().set("value", value),
                FindAndModifyOptions.options().returnNew(true),
                ConfigItem.class);
        if (configItem == null) {
            log.warn("Config update failed, key not found: {}", key);
        }
    }

    public List<ConfigItem> getSys() {
        return mongoTemplate.findAll(ConfigItem.class).stream().filter(ConfigItem::isVisible).toList();
    }

    public Boolean addTop(String id) {
        Submission submission = mongoTemplate.findById(id, Submission.class);
        Preconditions.checkNotNull(submission, AppException.resourceNotFound("submission"));
        topSubmissions.add(submission);
        this.setTopSubmissions(topSubmissions);
        return true;
    }

    public Boolean removeTop(String id) {
        Submission submission = mongoTemplate.findById(id, Submission.class);
        Preconditions.checkNotNull(submission, AppException.resourceNotFound("submission"));
        topSubmissions.remove(submission);
        this.setTopSubmissions(topSubmissions);
        return true;
    }

    public Boolean updateConfig(Map<String, String> config) {
        log.info("Admin update config: {}", config);
        config.forEach(this::updateConfig);
        return true;
    }
}

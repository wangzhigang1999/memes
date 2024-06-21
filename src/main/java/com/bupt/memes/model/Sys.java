package com.bupt.memes.model;

import com.bupt.memes.model.media.Submission;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("sys")
@Data
public class Sys {
    private String id = "sys";
    // 机器人是否开启
    Boolean botUp = true;
    // 当天的最少投稿数
    int MIN_SUBMISSIONS = 50;
    // 相似检索时的最大返回数
    int topK = 10;
    // 缓存的大小
    int submissionCacheSize = 1000;
    // 置顶的投稿的集合
    Set<Submission> topSubmission = Set.of();
    // 索引的版本
    long indexVersion = 0;
    // 索引的加载位置
    String indexFile = "hnsw.index";
}

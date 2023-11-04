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

    // 用户可见的最大历史日期数目
    int MAX_HISTORY = 7;

    // 一系列的投稿发布策略
    Set<String> releaseStrategy = Set.of();

    // 当前选中的发布策略
    String selectedReleaseStrategy = "default";

    // 置顶的投稿的集合
    Set<Submission> topSubmission = Set.of();
}

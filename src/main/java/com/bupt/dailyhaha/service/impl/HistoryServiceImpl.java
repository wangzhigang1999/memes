package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.Interface.History;
import com.bupt.dailyhaha.util.Utils;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 历史记录服务
 * <p>
 * meme图的设计中有日期的概念，每一天的所有投稿会被归档至一个 history记录中
 * 因此可以根据日期直接获取当天的投稿记录
 */
@Service
public class HistoryServiceImpl implements History {

    final MongoTemplate mongoTemplate;

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(HistoryServiceImpl.class);
    final static ConcurrentHashMap<String, com.bupt.dailyhaha.pojo.media.History> dateHistoryCache = new ConcurrentHashMap<>();

    public HistoryServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 获取历史记录
     *
     * @param date 日期 YYYY-MM-DD
     * @return 当天的记录
     */
    @Override
    public List<Submission> getHistory(String date) {
        if (dateHistoryCache.containsKey(date)) {
            logger.info("cache hit,date: {}", date);
            return dateHistoryCache.get(date).getSubmissions();
        }
        com.bupt.dailyhaha.pojo.media.History history = mongoTemplate.findOne(Query.query(Criteria.where("date").is(date)), com.bupt.dailyhaha.pojo.media.History.class);
        if (history != null) {
            logger.info("cache miss, date: {},will update it.", date);
            dateHistoryCache.put(date, history);
        }
        return history == null ? new ArrayList<>() : history.getSubmissions();
    }

    /**
     * 获取所有的历史记录的日期
     *
     * @param limit 限制数量
     * @return 日期列表
     */
    @Override
    public List<String> getHistoryDates(int limit) {
        Query query = Query.query(Criteria.where("date").exists(true));
        query.limit(limit).with(Sort.by(Sort.Direction.DESC, "timestamp")).fields().include("date").exclude("_id");

        return mongoTemplate.find(query, Map.class, "history").stream()
                .map(map -> map.get("date").toString())
                .filter(date -> !date.equals(Utils.getYMD()))
                .collect(Collectors.toList());
    }

    /**
     * 更新历史记录
     *
     * @param date        日期  YYYY-MM-DD
     * @param Submissions 投稿列表
     * @return 是否更新成功
     */
    @Override
    public boolean updateHistory(String date, List<Submission> Submissions) {
        com.bupt.dailyhaha.pojo.media.History history = new com.bupt.dailyhaha.pojo.media.History();
        history.setDate(date);
        history.setSubmissions(Submissions);
        history.setTimestamp(System.currentTimeMillis());

        Update update = new Update().set("Submissions", Submissions).set("timestamp", System.currentTimeMillis()).set("count", Submissions.size());
        UpdateResult result = mongoTemplate.upsert(Query.query(Criteria.where("date").is(date)), update, com.bupt.dailyhaha.pojo.media.History.class);
        var res = result.getUpsertedId() != null || result.getModifiedCount() > 0;

        if (res) {
            logger.info("update history success,update cache date: {}", date);
            dateHistoryCache.put(date, history);
        }
        return res;
    }
}

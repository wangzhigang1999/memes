package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.service.HistoryService;
import com.bupt.dailyhaha.service.ReviewService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    final MongoTemplate template;

    final HistoryService historyService;

    public ReviewServiceImpl(MongoTemplate template, HistoryService historyService) {
        this.template = template;
        this.historyService = historyService;
    }

    @Override
    public List<Submission> listSubmissions() {
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时,从上一天的22点开始算起
        var from = start - 2 * 60 * 60 * 1000;
        Criteria criteria = Criteria.where("timestamp").gte(from).and("deleted").ne(true).and("reviewed").ne(true);
        return template.find(Query.query(criteria), Submission.class);
    }

    @Override
    public boolean acceptSubmission(int hashcode) {
        return updateSubmission(hashcode, false);
    }

    @Override
    public boolean rejectSubmission(int hashcode) {
        return updateSubmission(hashcode, true);
    }

    @Override
    public boolean release() {
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时
        var from = start - 2 * 60 * 60 * 1000;
        // 向后推22个小时
        var to = start + 22 * 60 * 60 * 1000;
        Criteria criteria = Criteria.where("timestamp").gte(from).lte(to).and("deleted").ne(true).and("reviewed").ne(false);
        List<Submission> submissions = template.find(Query.query(criteria), Submission.class);
        String date = Utils.getYMD();

        return historyService.updateHistory(date, submissions);
    }


    private boolean updateSubmission(int hashcode, boolean deleted) {
        var query = new Query(Criteria.where("hash").is(hashcode));
        template.update(Submission.class).matching(query).apply(new Update().set("deleted", deleted).set("reviewed", true)).all();
        Submission one = template.findOne(query, Submission.class);
        return one != null && one.getDeleted();
    }

}

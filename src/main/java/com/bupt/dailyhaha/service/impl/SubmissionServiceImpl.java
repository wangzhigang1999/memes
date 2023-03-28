package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.submission.History;
import com.bupt.dailyhaha.pojo.submission.Submission;
import com.bupt.dailyhaha.service.SubmissionService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    final MongoTemplate mongoTemplate;

    public SubmissionServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public boolean deleteByName(String name) {
        var query = new Query(Criteria.where("name").is(name));
        mongoTemplate.update(Submission.class).matching(query).apply(new Update().set("deleted", true)).all();
        Submission one = mongoTemplate.findOne(query, Submission.class);
        return one != null && one.getDeleted();
    }

    @Override
    public boolean deleteByHashcode(int hashcode) {
        var query = new Query(Criteria.where("hash").is(hashcode));
        mongoTemplate.update(Submission.class).matching(query).apply(new Update().set("deleted", true)).all();
        Submission one = mongoTemplate.findOne(query, Submission.class);
        return one != null && one.getDeleted();
    }


    @Override
    public List<Submission> getTodaySubmissions() {
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时
        var from = start - 2 * 60 * 60 * 1000;

        // 向后推22个小时
        var to = start + 22 * 60 * 60 * 1000;
        return mongoTemplate.find(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("deleted").ne(true)), Submission.class);
    }


    @Override
    public boolean vote(int hashcode, boolean up) {
        // if up is true, then vote up, else vote down
        var query = new Query(Criteria.where("hash").is(hashcode));
        var update = new Update();
        if (up) {
            update.inc("up", 1);
        } else {
            update.inc("down", 1);
        }
        UpdateResult first = mongoTemplate.update(Submission.class).matching(query).apply(update).first();
        return first.getMatchedCount() > 0;
    }

    @Override
    public List<Submission> getLastHistory() {
        // 查询history表中最后一条记录
        History history = mongoTemplate.findOne(
                Query.query(Criteria.where("timestamp").exists(true))
                        .limit(1)
                        .with(Sort.by(Sort.Direction.DESC, "timestamp")), History.class);
        return history == null ? new ArrayList<>() : history.getSubmissions();
    }

    @Override
    public List<Submission> getHistory(String date) {
        History history = mongoTemplate.findOne(Query.query(Criteria.where("date").is(date)), History.class);
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
        List<History> histories = mongoTemplate.find(Query.query(Criteria.where("date").exists(true))
                .limit(limit)
                .with(Sort.by(Sort.Direction.DESC, "timestamp")), History.class);
        List<String> dates = new ArrayList<>();
        for (History history : histories) {
            dates.add(history.getDate());
        }
        return dates;
    }

    @Override
    public boolean updateHistory(String date, List<Submission> Submissions) {
        History history = new History();
        history.setDate(date);
        history.setSubmissions(Submissions);
        history.setTimestamp(System.currentTimeMillis());


        Update update = new Update().set("Submissions", Submissions).set("timestamp", System.currentTimeMillis()).set("count", Submissions.size());
        UpdateResult result = mongoTemplate.upsert(Query.query(Criteria.where("date").is(date)), update, History.class);
        return result.getUpsertedId() != null || result.getModifiedCount() > 0;
    }

    @Override
    public Submission storeTextFormatSubmission(String uri, String mime) {
        Submission submission = new Submission();
        submission.setSubmissionType(mime);
        submission.setName(uri);
        submission.setUrl(uri);
        submission.setHash(uri.hashCode());

        mongoTemplate.save(submission);
        return submission;
    }
}

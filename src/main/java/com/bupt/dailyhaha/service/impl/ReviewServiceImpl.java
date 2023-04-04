package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.Submission;
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

    public ReviewServiceImpl(MongoTemplate template) {
        this.template = template;
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



    private boolean updateSubmission(int hashcode, boolean deleted) {
        var query = new Query(Criteria.where("hash").is(hashcode));
        template.update(Submission.class).matching(query).apply(new Update().set("deleted", deleted).set("reviewed", true)).all();
        Submission one = template.findOne(query, Submission.class);
        return one != null && one.getDeleted();
    }

}

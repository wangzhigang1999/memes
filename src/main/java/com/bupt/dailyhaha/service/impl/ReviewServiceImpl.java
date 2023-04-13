package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.service.HistoryService;
import com.bupt.dailyhaha.service.ReleaseStrategy;
import com.bupt.dailyhaha.service.ReviewService;
import com.bupt.dailyhaha.service.SysConfig;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    final MongoTemplate template;

    final HistoryService historyService;

    final Map<String, ReleaseStrategy> releaseStrategyMap;

    final SysConfig config;


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
    public int batchAcceptSubmission(List<Integer> hashcode) {
        int count = 0;
        for (int i : hashcode) {
            if (acceptSubmission(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int release() {

        String date = Utils.getYMD();
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时
        var from = start - 2 * 60 * 60 * 1000;
        // 向后推22个小时
        var to = start + 22 * 60 * 60 * 1000;

        Criteria criteria = Criteria.where("timestamp").gte(from).lte(to).and("deleted").ne(true).and("reviewed").ne(false);
        List<Submission> submissions = template.find(Query.query(criteria), Submission.class);

        List<Submission> history = historyService.getHistory(date);
        List<Submission> newSubmissions = findDiff(history, submissions);

        var strategy = releaseStrategyMap.get(config.sys.getSelectedReleaseStrategy());
        if (strategy != null) {
            submissions = strategy.release(history, newSubmissions);
        }
        boolean updateHistory = historyService.updateHistory(date, submissions);
        return updateHistory ? submissions.size() : -1;
    }

    /**
     * 获取当前已经review的submission数量
     *
     * @return 当前已经review的submission数量
     */
    @Override
    public long getReviewedNum() {
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时,从上一天的22点开始算起
        var from = start - 2 * 60 * 60 * 1000;
        Criteria criteria = Criteria.where("timestamp").gte(from).and("deleted").ne(true).and("reviewed").ne(false);
        return template.count(Query.query(criteria), Submission.class);
    }


    private boolean updateSubmission(int hashcode, boolean deleted) {
        var query = new Query(Criteria.where("hash").is(hashcode));
        template.update(Submission.class).matching(query).apply(new Update().set("deleted", deleted).set("reviewed", true)).all();
        Submission one = template.findOne(query, Submission.class);
        return one != null && one.getDeleted();
    }

    private static List<Submission> findDiff(List<Submission> currentSubmissions, List<Submission> newSubmissions) {
        return newSubmissions.stream().filter(newSubmission -> currentSubmissions.stream().noneMatch(currentSubmission -> Objects.equals(currentSubmission, newSubmission))).toList();
    }

}

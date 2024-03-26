package com.bupt.memes.service.impl;

import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.Review;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.comparator.Comparators;

import java.util.List;
import java.util.Map;

import static com.bupt.memes.model.common.SubmissionCollection.*;
import static com.bupt.memes.util.TimeUtil.getTodayStartUnixEpochMilli;

/**
 * 审核相关的服务
 * 对于所有的投稿，都要人工的进行审核
 */
@Service
@AllArgsConstructor
public class ReviewServiceImpl implements Review {

    final MongoTemplate template;

    /**
     * 获取今天的所有没有审核的投稿
     *
     * @return 今天的所有没有审核的投稿
     */
    @Override
    public List<Submission> getWaitingSubmissions() {
        List<Submission> submissions = template.findAll(Submission.class, WAITING_SUBMISSION);
        submissions.sort(Comparators.comparable());
        return submissions;
    }

    @Override
    public List<Submission> getWaitingSubmissions(Integer limit) {
        var query = new Query();
        query.limit(limit);
        query.with(Sort.by(Sort.Direction.ASC, "timestamp"));
        return template.find(query, Submission.class, WAITING_SUBMISSION);
    }

    /**
     * 审核通过
     *
     * @param id submission 的 id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptSubmission(String id) {
        return reviewSubmission(id, true);
    }

    /**
     * 审核不通过
     *
     * @param id submission 的 id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectSubmission(String id) {
        return reviewSubmission(id, false);
    }

    /**
     * 批量通过
     *
     * @param ids submission 的 id 列表
     * @return 成功通过的数量
     */
    @Override
    public int batchAcceptSubmission(List<String> ids) {
        ids.sort(String::compareTo);
        ids.forEach(this::acceptSubmission);
        return ids.size();
    }

    /**
     * 批量拒绝
     * 
     * @param ids submission 的 id 列表
     * @return 成功拒绝的数量
     */
    @Override
    public int batchRejectSubmission(List<String> ids) {
        ids.sort(String::compareTo);
        ids.forEach(this::rejectSubmission);
        return ids.size();
    }

    /**
     * 获取当前已经 review 的 submission 数量
     *
     * @return 当前已经 review 的 submission 数量
     */
    @Override
    public long getPassedNum() {
        // 00:00:00 of today
        var start = getTodayStartUnixEpochMilli();
        // 向前推两个小时，从上一天的 22 点开始算起
        var from = start - 2 * 60 * 60 * 1000;
        Criteria criteria = Criteria.where("timestamp").gte(from);
        return template.count(Query.query(criteria), Submission.class);
    }

    @Override
    public long getWaitingNum() {
        // 00:00:00 of today
        var start = getTodayStartUnixEpochMilli();
        // 向前推两个小时，从上一天的 22 点开始算起
        var from = start - 2 * 60 * 60 * 1000;
        Criteria criteria = Criteria.where("timestamp").gte(from);
        return template.count(Query.query(criteria), Submission.class, WAITING_SUBMISSION);
    }

    @Override
    public Map<String, Long> getTodayInfo() {
        long passedNum = getPassedNum();
        long waitingNum = getWaitingNum();
        return Map.of("passedNum", passedNum, "waitingNum", waitingNum);
    }

    /**
     * 更新投稿的审核状态
     *
     * @param id     投稿的 id
     * @param passed 是否通过
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("null")
    public boolean reviewSubmission(String id, boolean passed) {
        var query = new Query(Criteria.where("id").is(id));
        // 当一个投稿被审核通过或者不通过的时候，就从等待审核的表中删除，然后插入到对应的表中
        var one = template.findAndRemove(query, Submission.class, WAITING_SUBMISSION);
        assert one != null;
        template.save(one, passed ? SUBMISSION : DELETED_SUBMISSION);
        return true;
    }

}

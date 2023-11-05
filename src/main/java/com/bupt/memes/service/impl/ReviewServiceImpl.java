package com.bupt.memes.service.impl;

import com.bupt.memes.model.media.Submission;
import com.bupt.memes.model.ws.WSPacket;
import com.bupt.memes.model.ws.WSPacketType;
import com.bupt.memes.service.Interface.IHistory;
import com.bupt.memes.service.Interface.ReleaseStrategy;
import com.bupt.memes.service.Interface.Review;
import com.bupt.memes.service.SysConfigService;
import com.bupt.memes.util.Utils;
import com.bupt.memes.ws.WebSocketEndpoint;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 审核相关的服务
 * 对于所有的投稿，都要人工的进行审核
 */
@Service
@AllArgsConstructor
public class ReviewServiceImpl implements Review {

    final MongoTemplate template;

    final IHistory history;

    final Map<String, ReleaseStrategy> releaseStrategyMap;

    final SysConfigService config;


    /**
     * 获取今天的所有没有审核的投稿
     *
     * @return 今天的所有没有审核的投稿
     */
    @Override
    public List<Submission> listSubmissions() {
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时,从上一天的22点开始算起
        var from = start - 2 * 60 * 60 * 1000;
        Criteria criteria = Criteria
                .where("timestamp").gte(from)
                .and("deleted").ne(true)
                .and("reviewed").ne(true);
        return template.find(Query.query(criteria), Submission.class);
    }

    /**
     * 审核通过
     *
     * @param hashcode submission的hashcode
     * @return 是否成功
     */
    @Override
    public boolean acceptSubmission(int hashcode) {
        return updateSubmission(hashcode, false);
    }

    /**
     * 审核不通过
     *
     * @param hashcode submission的hashcode
     * @return 是否成功
     */
    @Override
    public boolean rejectSubmission(int hashcode) {
        return updateSubmission(hashcode, true);
    }

    /**
     * 批量通过
     *
     * @param hashcode submission的hashcode列表
     * @return 成功通过的数量
     */
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

    /**
     * 发布
     *
     * @return 发布的数量
     */
    @Override
    public int release() {

        String date = Utils.getYMD();
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时
        var from = start - 2 * 60 * 60 * 1000;
        // 向后推22个小时
        var to = start + 22 * 60 * 60 * 1000;

        /*
            从上一天的 22:00:00 到今天的 22:00:00
         */

        Criteria criteria = Criteria
                .where("timestamp").gte(from).lte(to)
                .and("deleted").ne(true)
                .and("reviewed").ne(false);

        // 获取这个时间段内的所有投稿
        List<Submission> submissions = template.find(Query.query(criteria), Submission.class);

        // 有一部分投稿是已经发布过的
        List<Submission> history = this.history.getHistory(date);

        // 找出这一批投稿中，哪些是新的投稿
        List<Submission> newSubmissions = findDiff(history, submissions);

        // 获取发布策略
        var strategy = releaseStrategyMap.get(SysConfigService.sys.getSelectedReleaseStrategy());
        if (strategy != null) {
            // 发布
            submissions = strategy.release(history, newSubmissions);
        }
        // 更新历史记录
        boolean updateHistory = this.history.updateHistory(date, submissions);
        return updateHistory ? submissions.size() : -1;
    }

    /**
     * 获取当前已经review的submission数量
     *
     * @return 当前已经review的submission数量
     */
    @Override
    public long getReviewPassedNum() {
        // 00:00:00 of today
        var start = Utils.getTodayStartUnixEpochMilli();
        // 向前推两个小时,从上一天的22点开始算起
        var from = start - 2 * 60 * 60 * 1000;
        Criteria criteria = Criteria
                .where("timestamp").gte(from)
                .and("deleted").ne(true)
                .and("reviewed").ne(false);
        return template.count(Query.query(criteria), Submission.class);
    }

    @Override
    public Map<String, Integer> getTodayInfo() {
        long reviewPassedNum = getReviewPassedNum();
        int releasedNum = history.getHistory(Utils.getYMD()).size();
        int toBeReviewedNum = listSubmissions().size();
        return Map.of("reviewPassedNum", (int) reviewPassedNum, "releasedNum", releasedNum, "toBeReviewedNum", toBeReviewedNum);
    }


    /**
     * 更新投稿的审核状态
     *
     * @param hashcode 投稿的hashcode
     * @param deleted  是否删除
     * @return 是否成功
     */
    private boolean updateSubmission(int hashcode, boolean deleted) {
        var query = new Query(Criteria.where("hash").is(hashcode));
        template.update(Submission.class).matching(query).apply(new Update().set("deleted", deleted).set("reviewed", true)).all();
        Submission one = template.findOne(query, Submission.class);
        WebSocketEndpoint.broadcast(new WSPacket<>(one, WSPacketType.REVIEW));
        return one != null && one.getDeleted();
    }

    private static List<Submission> findDiff(List<Submission> currentSubmissions, List<Submission> newSubmissions) {
        return newSubmissions.stream().filter(newSubmission -> currentSubmissions.stream().noneMatch(currentSubmission -> Objects.equals(currentSubmission, newSubmission))).toList();
    }

}

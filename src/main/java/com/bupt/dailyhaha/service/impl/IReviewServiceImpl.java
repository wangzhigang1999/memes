package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.mapper.MSubmission;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.IHistory;
import com.bupt.dailyhaha.service.IReleaseStrategy;
import com.bupt.dailyhaha.service.IReview;
import com.bupt.dailyhaha.service.SysConfigService;
import com.bupt.dailyhaha.util.Utils;
import lombok.AllArgsConstructor;
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
public class IReviewServiceImpl implements IReview {

    final MSubmission submissionMapper;
    final IHistory IHistory;

    final Map<String, IReleaseStrategy> releaseStrategyMap;

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
        var now = System.currentTimeMillis();
        return submissionMapper.find(from, now, false, false);
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


        // 获取这个时间段内的所有投稿
        List<Submission> submissions = submissionMapper.find(from, to, false, true);

        // 有一部分投稿是已经发布过的
        List<Submission> history = this.IHistory.getHistory(date);

        // 找出这一批投稿中，哪些是新的投稿
        List<Submission> newSubmissions = findDiff(history, submissions);

        // 获取发布策略
        var strategy = releaseStrategyMap.get(config.sysConfig.getSelectedReleaseStrategy());
        if (strategy != null) {
            // 发布
            submissions = strategy.release(history, newSubmissions);
        }
        // 更新历史记录
        boolean updateHistory = this.IHistory.updateHistory(date, submissions);
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
        return submissionMapper.count(from, System.currentTimeMillis(), false, true);
    }

    @Override
    public Map<String, Integer> getTodayInfo() {
        long reviewPassedNum = getReviewPassedNum();
        int releasedNum = IHistory.getHistory(Utils.getYMD()).size();
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
        return submissionMapper.updateStatus(hashcode, deleted) > 0;
    }

    private static List<Submission> findDiff(List<Submission> currentSubmissions, List<Submission> newSubmissions) {
        if (currentSubmissions == null || newSubmissions == null) {
            return List.of();
        }
        return newSubmissions.stream().filter(newSubmission -> currentSubmissions.stream().noneMatch(currentSubmission -> Objects.equals(currentSubmission, newSubmission))).toList();
    }

}

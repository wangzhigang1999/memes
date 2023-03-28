package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.submission.Submission;

import java.util.List;

public interface SubmissionService {


    boolean deleteByName(String name);

    boolean deleteByHashcode(int hashcode);

    /**
     * 获取今日的提交,只有管理员才能看到
     */
    List<Submission> getTodaySubmissions();


    /**
     * 点赞或者点踩
     *
     * @param hashcode 对应投稿的名字
     * @param up       true为点赞，false为点踩
     * @return 是否成功
     */
    boolean vote(int hashcode, boolean up);

    /**
     * 获取历史记录的最后一条
     */
    List<Submission> getLastHistory();


    /**
     * 获取历史记录
     *
     * @param date 日期 YYYY-MM-DD
     * @return 历史记录
     */
    List<Submission> getHistory(String date);

    /**
     * 获取所有的历史记录的日期
     *
     * @param limit 限制数量
     * @return 日期列表
     */
    List<String> getHistoryDates(int limit);

    /**
     * 更新历史记录
     */
    boolean updateHistory(String date, List<Submission> submissions);

    Submission storeTextFormatSubmission(String url, String mime);
}

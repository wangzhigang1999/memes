package com.bupt.memes.service.Interface;

import com.bupt.memes.model.media.Submission;

import java.util.List;
import java.util.Map;

public interface Review {

    /**
     * 列出从上一天的22：00到现在的所有未审核的submission
     *
     * @return submission列表
     */
    List<Submission> listSubmissions();

    /**
     * 接受一个submission
     *
     * @param hashcode submission的hashcode
     * @return 是否接受成功
     */
    boolean acceptSubmission(int hashcode);

    /**
     * 拒绝一个submission
     *
     * @param hashcode submission的hashcode
     * @return 是否拒绝成功
     */
    boolean rejectSubmission(int hashcode);


    /**
     * 批量审核通过submission，因为在所有的投稿中，绝大多数都是有效的，
     * 因此在剔除了无效的之后，剩下的批量通过，节省时间
     *
     * @param hashcode submission的hashcode列表
     * @return 成功审核的数量
     */
    int batchAcceptSubmission(List<Integer> hashcode);


    /**
     * 发布当日，指从昨天的22：00到今日的22：00的所有已经过审核的submission
     *
     * @return 是否发布成功
     */
    int release();

    /**
     * 获取当前已经review通过的submission数量
     * 这些都会被发布到今日的投稿中
     *
     * @return 当前已经review的submission数量
     */
    long getReviewPassedNum();

    /**
     * 获取今日的统计信息
     *
     * @return 今日的统计信息
     */
    Map<String, Integer> getTodayInfo();
}
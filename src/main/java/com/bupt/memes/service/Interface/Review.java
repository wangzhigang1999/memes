package com.bupt.memes.service.Interface;

import com.bupt.memes.model.media.Submission;

import java.util.List;
import java.util.Map;

public interface Review {

    /**
     * 列出所有未审核的 submission
     *
     * @return submission 列表
     */
    List<Submission> getWaitingSubmissions();

    List<Submission> getWaitingSubmissions(Integer limit);

    /**
     * 接受一个 submission
     *
     * @param id submission 的 id
     * @return 是否接受成功
     */
    boolean acceptSubmission(String id);

    /**
     * 拒绝一个 submission
     *
     * @param id submission 的 id
     * @return 是否拒绝成功
     */
    boolean rejectSubmission(String id);

    /**
     * 批量审核通过 submission，因为在所有的投稿中，绝大多数都是有效的，
     * 因此在剔除了无效的之后，剩下的批量通过，节省时间
     *
     * @param ids submission 的 id 列表
     * @return 成功审核的数量
     */
    int batchAcceptSubmission(List<String> ids);

    int batchRejectSubmission(List<String> ids);

    /**
     * 获取当前已经 review 通过的 submission 数量
     * 这些都会被发布到今日的投稿中
     *
     * @return 当前已经 review 的 submission 数量
     */
    long getPassedNum();

    long getWaitingNum();

    /**
     * 获取今日的统计信息
     *
     * @return 今日的统计信息
     */
    Map<String, Long> getTodayInfo();
}

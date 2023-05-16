package com.bupt.dailyhaha.service.Interface;

import com.bupt.dailyhaha.pojo.media.Submission;

import java.util.List;

public interface History {

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

}

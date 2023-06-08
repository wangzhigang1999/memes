package com.bupt.dailyhaha.mapper.impl;

import com.bupt.dailyhaha.mapper.MHistory;
import com.bupt.dailyhaha.mapper.mysql.MSubmission;
import com.bupt.dailyhaha.pojo.media.History;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.util.Utils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryMySQLImpl implements MHistory {

    final MSubmission submissionMapper;

    public HistoryMySQLImpl(MSubmission submissionMapper) {
        this.submissionMapper = submissionMapper;
    }

    @Override
    public History findByDate(String date) {
        // todo 还有一种实现方式是直接查询date，需要比较一下二者的性能
        // date format: YYYY-MM-DD, convert date to timestamp
        long startEpochMilli = Utils.getStartEpochMilli(date);
        var endEpochMilli = startEpochMilli + 24 * 60 * 60 * 1000;
        List<Submission> submissions = submissionMapper.find(startEpochMilli, endEpochMilli, 0, 1);
        History history = new History();
        history.setDate(date);
        history.setSubmissions(submissions);
        history.setCount(submissions.size());
        return history;
    }

    @Override
    public boolean updateHistory(String date, History history) {
        return true;
    }

    /**
     * 获取可用的日期
     *
     * @param limit 限制返回的日期数量
     * @return List<String>
     */
    @Override
    public List<String> findAvailableDates(int limit) {
        List<String> availableDates = submissionMapper.findAvailableDates(limit);
        String today = Utils.getYMD();
        availableDates.removeIf(date -> date.equals(today));
        return availableDates;
    }

}

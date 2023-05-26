package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.mapper.MHistory;
import com.bupt.dailyhaha.pojo.media.History;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.IHistory;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 历史记录服务
 * <p>
 * meme图的设计中有日期的概念，每一天的所有投稿会被归档至一个 history记录中
 * 因此可以根据日期直接获取当天的投稿记录
 */
@Service
@AllArgsConstructor
public class HistoryServiceImpl implements IHistory {


    final static Logger logger = org.slf4j.LoggerFactory.getLogger(HistoryServiceImpl.class);
    final MHistory historyMapper;
    final static ConcurrentHashMap<String, com.bupt.dailyhaha.pojo.media.History> dateHistoryCache = new ConcurrentHashMap<>();

    /**
     * 获取历史记录
     *
     * @param date 日期 YYYY-MM-DD
     * @return 当天的记录
     */
    @Override
    public List<Submission> getHistory(String date) {
        if (dateHistoryCache.containsKey(date)) {
            logger.info("cache hit,date: {}", date);
            return dateHistoryCache.get(date).getSubmissions();
        }

        var history = historyMapper.findByDate(date);
        if (history != null) {
            logger.info("cache miss, date: {},will update it.", date);
            dateHistoryCache.put(date, history);
        }
        return history == null ? new ArrayList<>() : history.getSubmissions();
    }

    /**
     * 获取所有的历史记录的日期
     *
     * @param limit 限制数量
     * @return 日期列表
     */
    @Override
    public List<String> getHistoryDates(int limit) {
        return historyMapper.findAvailableDates(limit);
    }

    /**
     * 更新历史记录
     *
     * @param date        日期  YYYY-MM-DD
     * @param Submissions 投稿列表
     * @return 是否更新成功
     */
    @Override
    public boolean updateHistory(String date, List<Submission> Submissions) {
        History history = new History();
        history.setDate(date);
        history.setSubmissions(Submissions);
        history.setTimestamp(System.currentTimeMillis());

        var res = historyMapper.updateHistory(date, history);

        if (res) {
            logger.info("update history success,update cache date: {}", date);
            dateHistoryCache.put(date, history);
        }
        return res;
    }
}

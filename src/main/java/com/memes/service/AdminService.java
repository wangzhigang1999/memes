package com.memes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memes.mapper.MediaMapper;
import com.memes.mapper.RequestLogMapper;
import com.memes.mapper.SubmissionMapper;
import com.memes.model.pojo.MediaContent;
import com.memes.model.pojo.RequestLog;
import com.memes.model.response.VisitStatistic;
import com.memes.util.TimeUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class AdminService {
    private final RequestLogMapper requestLogMapper;
    private final SubmissionMapper submissionMapper;
    private final MediaMapper mediaMapper;

    private final static String TIME_COLUMN = "created_at";

    /**
     * 获取访问统计信息
     *
     * @param date
     *            日期 YYYY-MM-DD
     * @return 访问统计信息
     */
    public VisitStatistic getVisitStatistic(String date) {
        log.info("get visit statistic for date {}", date);
        LocalDateTime startTime = TimeUtil.convertYMDToLocalDateTime(date);
        QueryWrapper<RequestLog> wrapper = new QueryWrapper<>();
        wrapper.between(TIME_COLUMN, startTime, startTime.plusDays(1));
        List<RequestLog> requestLogs = requestLogMapper.selectList(wrapper);

        VisitStatistic visitStatistic = new VisitStatistic();
        visitStatistic.setRequestNumber(requestLogs.size());
        visitStatistic
            .setAverageLatency(
                requestLogs.stream().mapToDouble(RequestLog::getTimecost).average().orElse(0));

        // UUID 统计信息
        Map<String, List<RequestLog>> uuidGroups = requestLogs
            .stream()
            .filter(log -> log.getUuid() != null)
            .collect(Collectors.groupingBy(RequestLog::getUuid));
        List<VisitStatistic.UidStat> uidStats = uuidGroups
            .entrySet()
            .stream()
            .map(entry -> {
                LogStats stats = calculateStats(entry.getValue());
                VisitStatistic.UidStat stat = new VisitStatistic.UidStat();
                stat.setUuid(entry.getKey());
                stat.setFirstTime(stats.firstTimestamp());
                stat.setLastTime(stats.lastTimestamp());
                stat.setAvg(stats.avgTimeCost());
                stat.setMin(stats.minTimeCost());
                stat.setMax(stats.maxTimeCost());
                stat.setCount(stats.count());
                return stat;
            })
            .collect(Collectors.toList());
        visitStatistic.setUidStats(uidStats);

        // URL 统计信息
        Map<String, List<RequestLog>> urlGroups = requestLogs
            .stream()
            .collect(Collectors.groupingBy(log -> "%s %s".formatted(log.getMethod(), log.getUrl())));
        List<VisitStatistic.UrlStat> urlStats = urlGroups
            .entrySet()
            .stream()
            .map(entry -> {
                LogStats stats = calculateStats(entry.getValue());
                String[] parts = entry.getKey().split(" ", 2);
                VisitStatistic.UrlStat stat = new VisitStatistic.UrlStat();
                stat.setMethod(parts[0]);
                stat.setUrl(parts.length > 1 ? parts[1] : "");
                stat.setFirstTime(stats.firstTimestamp());
                stat.setLastTime(stats.lastTimestamp());
                stat.setAvg(stats.avgTimeCost());
                stat.setMin(stats.minTimeCost());
                stat.setMax(stats.maxTimeCost());
                stat.setCount(stats.count());
                return stat;
            })
            .collect(Collectors.toList());
        visitStatistic.setUrlStat(urlStats);

        return visitStatistic;
    }

    private record LogStats(long firstTimestamp, long lastTimestamp, double avgTimeCost, int minTimeCost, int maxTimeCost, int count) {
    }

    private static LogStats calculateStats(List<RequestLog> logs) {
        long firstTimestamp = logs.stream().mapToLong(RequestLog::getTimestamp).min().orElse(0);
        long lastTimestamp = logs.stream().mapToLong(RequestLog::getTimestamp).max().orElse(0);
        var summary = logs.stream().mapToInt(RequestLog::getTimecost).summaryStatistics();
        return new LogStats(firstTimestamp, lastTimestamp, summary.getAverage(), summary.getMin(), summary.getMax(), logs.size());
    }

    /**
     * 获取审核统计信息,只统计当前的
     *
     * @return 审核统计信息
     */
    public Map<String, Long> getReviewStatistic() {
        LocalDateTime startTime = TimeUtil.convertYMDToLocalDateTime(TimeUtil.getYMD());
        LocalDateTime endTime = startTime.plusDays(1); // 直接加一天，不需要手动计算毫秒

        List<MediaContent> mediaContents = mediaMapper
            .selectList(
                new QueryWrapper<MediaContent>()
                    .ge(TIME_COLUMN, startTime)
                    .lt(TIME_COLUMN, endTime)
                    .select("status"));

        return mediaContents
            .stream()
            .collect(Collectors.groupingBy(content -> content.getStatus().toString(), Collectors.counting()));

    }

}

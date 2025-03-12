package com.memes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memes.mapper.RequestLogMapper;
import com.memes.model.pojo.RequestLog;
import com.memes.model.response.VisitStatistic;
import com.memes.util.TimeUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AdminService {
    private final RequestLogMapper requestLogMapper;

    public VisitStatistic getVisitStatistic(String date) {

        long startTime = TimeUtil.convertYMDToUnixEpochMilli(date);
        long endTime = startTime + 24 * 60 * 60 * 1000;

        List<RequestLog> requestLogs = requestLogMapper
            .selectList(
                new QueryWrapper<RequestLog>()
                    .ge("timestamp", startTime)
                    .lt("timestamp", endTime));

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
        List<VisitStatistic.UUIDStat> uuidStats = uuidGroups
            .entrySet()
            .stream()
            .map(entry -> {
                LogStats stats = calculateStats(entry.getValue());
                VisitStatistic.UUIDStat stat = new VisitStatistic.UUIDStat();
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
        visitStatistic.setUUIDStat(uuidStats);

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

    private static LogStats calculateStats(List<RequestLog> logs) {
        long firstTimestamp = logs.stream().mapToLong(RequestLog::getTimestamp).min().orElse(0);
        long lastTimestamp = logs.stream().mapToLong(RequestLog::getTimestamp).max().orElse(0);
        var summary = logs.stream().mapToInt(RequestLog::getTimecost).summaryStatistics();
        return new LogStats(firstTimestamp, lastTimestamp, summary.getAverage(), summary.getMin(), summary.getMax(), logs.size());
    }

    private record LogStats(long firstTimestamp, long lastTimestamp, double avgTimeCost, int minTimeCost, int maxTimeCost, int count) {
    }
}

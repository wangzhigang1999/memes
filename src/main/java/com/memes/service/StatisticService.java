package com.memes.service;

import com.memes.model.common.LogDocument;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.memes.util.TimeUtil.getTodayStartUnixEpochMilli;

@Service
public class StatisticService {

    final MongoTemplate template;

    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public StatisticService(MongoTemplate template) {
        this.template = template;
    }

    @SneakyThrows
    @SuppressWarnings("rawtypes")
    public Map<String, Object> statistic() {
        final long start = getTodayStartUnixEpochMilli();
        final long end = System.currentTimeMillis();

        Future<Long> total = executorService.submit(() -> {
            Query query = Query.query(Criteria.where("timestamp").gte(start).lte(end));
            return template.count(query, LogDocument.class);
        });

        Future<Double> averageCost = executorService.submit(() -> {
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                    Aggregation.group().avg("timecost").as("avgTimecost"));
            return (Double) template.aggregate(aggregation, LogDocument.class, Map.class).getMappedResults().getFirst()
                    .get("avgTimecost");
        });

        Future<List<Map>> uuidCountMap = executorService.submit(() -> executeAggregation(start, end, "uuid"));
        Future<List<Map>> urlCountMap = executorService.submit(() -> executeAggregation(start, end, "url", "method"));

        return Map.of(
                "reqNumber", total.get(),
                "uuidCountMap", uuidCountMap.get(),
                "urlCountMap", urlCountMap.get(),
                "averageCost", averageCost.get());

    }

    @SuppressWarnings("rawtypes")
    private List<Map> executeAggregation(long start, long end, String... groupBy) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                Aggregation.group(groupBy)
                        .count().as("count")
                        .avg("timecost").as("avg")
                        .max("timecost").as("max")
                        .min("timecost").as("min")
                        .first("timestamp").as("firstTime")
                        .last("timestamp").as("lastTime"),
                Aggregation.sort(Sort.Direction.DESC, "count"));

        return template.aggregate(aggregation, LogDocument.class, Map.class).getMappedResults().stream().map(StatisticService::flattenMap)
                .collect(Collectors.toList());

    }

    public static Map<String, Object> flattenMap(Map<String, Object> map) {
        return map.entrySet().stream()
                .flatMap(entry -> flattenEntry(entry).entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map<String, Object> flattenEntry(Map.Entry<String, Object> entry) {
        Map<String, Object> result = new HashMap<>();
        String newPrefix = entry.getKey();
        if (entry.getValue() instanceof Map) {
            ((Map<?, ?>) entry.getValue()).forEach((key, value) -> result.putAll(flattenEntry(new AbstractMap.SimpleEntry<>(key.toString(), value))));
        } else {
            result.put(newPrefix, entry.getValue());
        }
        return result;
    }

}

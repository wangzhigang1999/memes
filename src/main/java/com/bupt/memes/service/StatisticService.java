package com.bupt.memes.service;

import com.bupt.memes.model.common.LogDocument;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.bupt.memes.util.TimeUtil.getTodayStartUnixEpochMilli;

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
                    Aggregation.group().avg("timecost").as("avgTimecost")
            );
            return (Double) template.aggregate(aggregation, LogDocument.class, Map.class).getMappedResults().get(0).get("avgTimecost");
        });

        Future<List<Map>> uuidCountMap = executorService.submit(() -> executeAggregation(start, end, "uuid"));
        Future<List<Map>> urlCountMap = executorService.submit(() -> executeAggregation(start, end, "url"));

        return Map.of(
                "reqNumber", total.get(),
                "uuidCountMap", uuidCountMap.get(),
                "urlCountMap", urlCountMap.get(),
                "averageCost", averageCost.get()
        );

    }

    @SuppressWarnings("rawtypes")
    private List<Map> executeAggregation(long start, long end, String groupBy) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                Aggregation.group(groupBy)
                        .count().as("count")
                        .avg("timecost").as("avgTimecost")
                        .max("timecost").as("maxTimecost")
                        .min("timecost").as("minTimecost"),
                Aggregation.sort(Sort.Direction.DESC, "count"));

        return template.aggregate(aggregation, LogDocument.class, Map.class).getMappedResults();
    }

}

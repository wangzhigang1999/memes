package com.bupt.memes.service;

import com.bupt.memes.pojo.common.LogDocument;
import com.bupt.memes.util.Utils;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class StatisticService {


    final MongoTemplate template;

    public StatisticService(MongoTemplate template) {
        this.template = template;
    }

    @SneakyThrows
    public Map<String, Object> statistic() {
        // start is the timestamp of 00:00:00 of today asia/shanghai
        final long start = Utils.getTodayStartUnixEpochMilli();
        final long end = System.currentTimeMillis();

        AtomicLong total = new AtomicLong(0);
        AtomicReference<Double> averageCost = new AtomicReference<>(0.0);
        AtomicReference<Object> urlCountMap = new AtomicReference<>();
        AtomicReference<Object> uuidCountMap = new AtomicReference<>();

        CountDownLatch latch = new CountDownLatch(4);

        // query the total number of records
        Thread.ofVirtual().start(() -> {
            Query query = Query.query(Criteria.where("timestamp").gte(start).lte(end));
            total.set(template.count(query, LogDocument.class));
            latch.countDown();
        });

        Thread.ofVirtual().start(
                () -> {
                    var avg = template.aggregate(Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                            Aggregation.group().avg("timecost").as("avgTimecost")
                    ), LogDocument.class, Map.class).getMappedResults().get(0).get("avgTimecost");
                    averageCost.set((Double) avg);
                    latch.countDown();
                }
        );

        Thread.ofVirtual().start(
                () -> {
                    var countMap = template.aggregate(Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                            Aggregation.group("url").count().as("count")
                                    .avg("timecost").as("avgTimecost")
                                    .max("timecost").as("maxTimecost")
                                    .min("timecost").as("minTimecost"),
                            Aggregation.sort(Sort.Direction.DESC, "count")
                    ), LogDocument.class, Map.class).getMappedResults();
                    urlCountMap.set(countMap);
                    latch.countDown();
                }
        );

        Thread.ofVirtual().start(
                () -> {
                    // count by uuid,rename '_id' to key
                    var uuidMap = template.aggregate(Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                            Aggregation.group("uuid").count().as("count")
                                    .avg("timecost").as("avgTimecost")
                                    .first("timestamp").as("firstTime")
                                    .last("timestamp").as("lastTime"),
                            Aggregation.sort(Sort.Direction.DESC, "count")
                    ), LogDocument.class, Map.class).getMappedResults();
                    uuidCountMap.set(uuidMap);
                    latch.countDown();
                }
        );

        latch.await();


        return Map.of(
                "reqNumber", total.get(),
                "uuidCountMap", uuidCountMap.get(),
                "urlCountMap", urlCountMap.get(),
                "averageCost", averageCost.get()
        );


    }


}

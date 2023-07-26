package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.common.LogDocument;
import com.bupt.dailyhaha.util.Utils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatisticService {


    final MongoTemplate template;

    public StatisticService(MongoTemplate template) {
        this.template = template;
    }

    public Map<String, Object> statistic() {
        // start is the timestamp of 00:00:00 of today asia/shanghai
        long start = Utils.getTodayStartUnixEpochMilli();
        long end = System.currentTimeMillis();

        // query the total number of records
        Query query = Query.query(Criteria.where("timestamp").gte(start).lte(end));
        long total = template.count(query, LogDocument.class);

        // sum all timecost from today
        var averageCost = template.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                Aggregation.group().avg("timecost").as("avgTimecost")
        ), LogDocument.class, Map.class).getMappedResults().get(0).get("avgTimecost");

        // count by url
        var urlCountMap = template.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                Aggregation.group("url").count().as("count")
                        .avg("timecost").as("avgTimecost")
                        .max("timecost").as("maxTimecost")
                        .min("timecost").as("minTimecost"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        ), LogDocument.class, Map.class).getMappedResults();


        // count by uuid,rename '_id' to key
        var uuidCountMap = template.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").gte(start).lte(end)),
                Aggregation.group("uuid").count().as("count")
                        .avg("timecost").as("avgTimecost")
                        .first("timestamp").as("firstTime")
                        .last("timestamp").as("lastTime"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        ), LogDocument.class, Map.class).getMappedResults();


        return Map.of(
                "reqNumber", total,
                "uuidCountMap", uuidCountMap,
                "urlCountMap", urlCountMap,
                "averageCost", averageCost
        );


    }

}

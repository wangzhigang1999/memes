package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.LogDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticService {


    final MongoTemplate template;

    public StatisticService(MongoTemplate template) {
        this.template = template;
    }

    /**
     * load records from start to end
     *
     * @param start start timestamp
     * @param end   end timestamp
     * @return List<Map < String, Object>>
     */

    public List<LogDocument> loadRecords(long start, long end) {
        Query query = new Query();
        query.addCriteria(Criteria.where("timestamp").gte(start).lte(end));
        query.fields().include("timestamp").include("url").include("ip").include("timecost").include("uuid");
        List<LogDocument> documents = template.find(query, LogDocument.class);
        // filter the records which has :empty uuid or :empty url or :empty ip or :empty timecost
        documents.removeIf(logDocument -> logDocument.getUuid().isEmpty());
        return documents;
    }

    public Map<String, Object> statistic() {
        // start is the timestamp of 00:00:00 of today asia/shanghai
        long start = Utils.getTodayStartUnixEpochMilli();
        long end = System.currentTimeMillis();
        List<LogDocument> logDocuments = loadRecords(start, end);

        HashMap<String, Object> map = new HashMap<>();

        // count the number of records
        map.put("reqNumber", logDocuments.size());

        // frequency of each url
        HashMap<String, Object> urlCountMap = new HashMap<>();
        logDocuments.stream().map(LogDocument::getUrl).distinct().forEach(url -> {
            long count = logDocuments.stream().filter(logDocument -> logDocument.getUrl().equals(url)).count();
            urlCountMap.put(url, count);
        });

        // frequency of each ip
        HashMap<String, Object> ipCountMap = new HashMap<>();
        logDocuments.stream().map(LogDocument::getIp).distinct().forEach(ip -> {
            long count = logDocuments.stream().filter(logDocument -> logDocument.getIp().equals(ip)).count();
            ipCountMap.put(ip, count);
        });


        // most frequent uuid
        Map<String, Object> uuidCountMap = new HashMap<>();
        logDocuments.stream().map(LogDocument::getUuid).distinct().forEach(uuid -> {
            long count = logDocuments.stream().filter(logDocument -> logDocument.getUuid().equals(uuid)).count();
            uuidCountMap.put(uuid, count);
        });

        map.put("uuidCountMap", uuidCountMap);
        map.put("urlCountMap", urlCountMap);
        map.put("ipCountMap", ipCountMap);


        // count the average cost,max cost,min cost
        DoubleSummaryStatistics statistics = logDocuments.stream().mapToDouble(LogDocument::getTimecost).summaryStatistics();
        map.put("averageCost", statistics.getAverage());
        map.put("maxCost", statistics.getMax());
        map.put("minCost", statistics.getMin());

        return map;


    }

}

package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.common.LogDocument;
import com.bupt.dailyhaha.pojo.common.Pair;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

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


    public static Map<String, Long> getCountMap(List<LogDocument> logDocuments, Function<LogDocument, String> keyExtractor) {
        Map<String, Long> countMap = new HashMap<>();
        for (LogDocument logDocument : logDocuments) {
            String key = keyExtractor.apply(logDocument);
            Long count = countMap.get(key);
            if (count == null) {
                count = 0L;
            }
            count = count + 1L;
            countMap.put(key, count);
        }
        return countMap;
    }

    /**
     * flatten the map and sort it by value
     *
     * @param map the map to be flattened
     * @return List<Pair < String, Long>>
     */
    public static List<Pair<String, Long>> flattenAndSort(Map<String, Long> map) {
        var list = new ArrayList<>(map.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).toList());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return list;
    }

    public Map<String, Object> statistic() {
        // start is the timestamp of 00:00:00 of today asia/shanghai
        long start = Utils.getTodayStartUnixEpochMilli();
        long end = System.currentTimeMillis();
        List<LogDocument> logDocuments = loadRecords(start, end);

        HashMap<String, Object> map = new HashMap<>();

        // count the number of records
        map.put("reqNumber", logDocuments.size());

        // usage
        Map<String, Long> urlCountMap = getCountMap(logDocuments, LogDocument::getUrl);
        Map<String, Long> ipCountMap = getCountMap(logDocuments, LogDocument::getIp);
        Map<String, Long> uuidCountMap = getCountMap(logDocuments, LogDocument::getUuid);


        map.put("uuidCountMap", flattenAndSort(uuidCountMap));
        map.put("urlCountMap", flattenAndSort(urlCountMap));
        map.put("ipCountMap", flattenAndSort(ipCountMap));


        // count the average cost,max cost,min cost
        DoubleSummaryStatistics statistics = logDocuments.stream().mapToDouble(LogDocument::getTimecost).summaryStatistics();
        map.put("averageCost", statistics.getAverage());
        map.put("maxCost", statistics.getMax());
        map.put("minCost", statistics.getMin());

        return map;


    }

}

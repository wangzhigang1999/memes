package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.mapper.MLog;
import com.bupt.dailyhaha.pojo.common.LogDocument;
import com.bupt.dailyhaha.pojo.common.Pair;
import com.bupt.dailyhaha.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class StatisticService {


    MLog logMapper;


    /**
     * load records from start to end
     *
     * @param start start timestamp
     * @param end   end timestamp
     * @return List<Map < String, Object>>
     */
    public List<LogDocument> loadRecords(long start, long end) {
        return logMapper.find(start, end);
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
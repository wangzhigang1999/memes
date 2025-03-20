package com.memes.model.response;

import java.util.List;

import lombok.Data;

@Data
public class VisitStatistic {

    private Integer requestNumber;
    private List<UidStat> uidStats;
    private Double averageLatency;
    private List<UrlStat> urlStat;

    @Data
    public static class UidStat {
        private String uuid;
        private Long firstTime;
        private Long lastTime;
        private Double avg;
        private Integer min;
        private Integer max;
        private Integer count;
    }

    @Data
    public static class UrlStat {
        private String url;
        private Long firstTime;
        private Long lastTime;
        private Double avg;
        private Integer min;
        private Integer max;
        private String method;
        private Integer count;
    }
}

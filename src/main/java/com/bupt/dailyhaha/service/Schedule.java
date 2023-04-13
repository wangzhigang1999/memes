package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.Utils;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Schedule {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(Schedule.class);

    final ReviewService reviewService;

    final SysConfig sysConfig;

    public Schedule(ReviewService reviewService, SysConfig sysConfig) {
        this.reviewService = reviewService;
        this.sysConfig = sysConfig;
    }


    /**
     * 每隔 30min 自动发布一次
     */
    @Scheduled(fixedRate = 1000 * 1800)
    public void autoRelease() {
        int release = reviewService.release();
        logger.info("release {} submissions", release);

        // 每次发布后，检查是否需要更新配置
        if (release >= sysConfig.getMaxSubmissions()) {
            sysConfig.disableBot();
            logger.info("disable bot,because submissions is full. Release {} submissions,Limit {}", release, sysConfig.getMaxSubmissions());
        }

        // 十点之后开启机器人
        int currentHour = Utils.getCurrentHour();
        if (currentHour >= 22 || currentHour <= 8) {
            sysConfig.enableBot();
            logger.info("enable bot,because current hour is {}", currentHour);
        }
    }
}

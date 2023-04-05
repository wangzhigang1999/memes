package com.bupt.dailyhaha.service;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Schedule {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(Schedule.class);

    final ReviewService reviewService;

    public Schedule(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    /**
     * 每隔 30min 自动发布一次
     */
    @Scheduled(fixedRate = 1000 * 1800)
    public void autoRelease() {
        logger.info("auto release");
        reviewService.release();
    }
}

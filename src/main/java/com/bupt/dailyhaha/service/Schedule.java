package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.media.Submission;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Schedule {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(Schedule.class);

    final ReviewService reviewService;

    final Storage storage;

    final SubmissionService submission;

    final SysConfig sysConfig;

    public Schedule(ReviewService reviewService, Storage storage, SubmissionService submission, SysConfig sysConfig) {
        this.reviewService = reviewService;
        this.storage = storage;
        this.submission = submission;
        this.sysConfig = sysConfig;
    }


    /**
     * 每隔 30min 自动发布一次
     */
    @Scheduled(fixedRate = 1000 * 1800)
    public void autoRelease() {
        int release = reviewService.release();
        logger.info("release {} submissions", release);

        int toBeReviewed = reviewService.listSubmissions().size();
        long reviewPassedNum = reviewService.getReviewPassedNum();
        int targetNum = sysConfig.getMaxSubmissions();

        boolean botShouldEnabled = shouldBotEnabled(reviewPassedNum, toBeReviewed, targetNum);

        // 十点之后开启机器人
        int currentHour = Utils.getCurrentHour();
        if (currentHour >= 22 || currentHour <= 8) {
            botShouldEnabled = true;
            logger.info("bot enabled because of time");
        }

        if (botShouldEnabled) {
            sysConfig.enableBot();
            logger.info("bot enabled");
        } else {
            sysConfig.disableBot();
            logger.info("bot disabled");
        }
    }

    /**
     * 判断机器人是否应该开启
     *
     * @param curSubmissionNum 当前已审核通过的数量
     * @param toBeReviewed     待审核的数量
     * @param targetNum        目标数量
     * @return 是否应该开启
     */
    public static boolean shouldBotEnabled(long curSubmissionNum, int toBeReviewed, int targetNum) {
        // 如果当前已发布的数量大于目标数量，直接返回false
        if (curSubmissionNum >= targetNum) {
            logger.info("current submission num {} is larger than target num {}.", curSubmissionNum, targetNum);
            return false;
        }
        int needed = (int) (targetNum - curSubmissionNum);
        logger.info("needed: {}, toBeReviewed: {},at least: {}", needed, toBeReviewed, needed * 1.5);

        // 审核的通过率大概是2/3,所以需要的数量是1.5倍
        return toBeReviewed < (needed * 1.5);
    }

    /**
     * 每天一次，清理一次存储
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanImg() {
        List<Submission> deletedSubmission = submission.getDeletedSubmission();
        logger.info("clean {} images", deletedSubmission.size());

        if (deletedSubmission.size() == 0) {
            return;
        }

        String[] keys = new String[deletedSubmission.size()];
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < deletedSubmission.size(); i++) {
            keys[i] = deletedSubmission.get(i).getName();
            map.put(keys[i], deletedSubmission.get(i).getHash());
        }

        HashMap<String, Boolean> booleanHashMap = storage.delete(keys);
        if (booleanHashMap == null) {
            logger.error("clean images failed");
            return;
        }

        for (Map.Entry<String, Boolean> entry : booleanHashMap.entrySet()) {
            if (entry.getValue()) {
                submission.hardDeleteSubmission(map.get(entry.getKey()));
            }
        }

        logger.info("clean images done");
    }


}

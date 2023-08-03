package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.service.Interface.Review;
import com.bupt.dailyhaha.service.Interface.Storage;
import com.bupt.dailyhaha.service.Interface.Submission;
import com.bupt.dailyhaha.util.Utils;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务
 */

@Service
public class Schedule {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(Schedule.class);

    final Review review;

    final Storage storage;

    final Submission submission;

    final SysConfigService sysConfig;

    public Schedule(Review review, Storage storage, Submission submission, SysConfigService sysConfig) {
        this.review = review;
        this.storage = storage;
        this.submission = submission;
        this.sysConfig = sysConfig;
    }


    /**
     * 每隔 30min 自动发布一次
     */
    @Scheduled(fixedRate = 1000 * 1800)
    public void autoRelease() {
        int release = review.release();
        logger.info("auto release {} submissions", release);


        int toBeReviewed = review.listSubmissions().size();
        long reviewPassedNum = review.getReviewPassedNum();
        int targetNum = sysConfig.getSys().getMAX_HISTORY();

        boolean botShouldEnabled = shouldBotEnabled(reviewPassedNum, toBeReviewed, targetNum);

        // 十点之后开启机器人
        int currentHour = Utils.getCurrentHour();
        if (currentHour >= 21 || currentHour <= 8) {
            botShouldEnabled = true;
            logger.info("bot should enable because of time: {}", currentHour);
        }

        if (botShouldEnabled) {
            sysConfig.enableBot();
        } else {
            sysConfig.disableBot();
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
            logger.info("bot should disable because of curSubmissionNum: {} more than targetNum: {}", curSubmissionNum, targetNum);
            return false;
        }
        int needed = (int) (targetNum - curSubmissionNum);

        // 审核的通过率大概是2/3,所以需要的数量是1.5倍
        boolean b = toBeReviewed < (needed * 1.5);
        logger.info("bot should {} because of toBeReviewed: {} needed: {}", b ? "enable" : "disable", toBeReviewed, needed);
        return b;
    }

    /**
     * 每天一次，清理一次存储
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanImg() {
        List<com.bupt.dailyhaha.pojo.media.Submission> deletedSubmission = submission.getDeletedSubmission();
        logger.info("clean images, {} images to be deleted", deletedSubmission.size());

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
            logger.error("clean images failed,because of storage error");
            return;
        }

        for (Map.Entry<String, Boolean> entry : booleanHashMap.entrySet()) {
            if (entry.getValue()) {
                submission.hardDeleteSubmission(map.get(entry.getKey()));
            }
        }

        logger.info("clean images done, {} images deleted", booleanHashMap.size());
    }


}

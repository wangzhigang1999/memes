package com.bupt.memes.service;

import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.service.Interface.Review;
import com.bupt.memes.service.Interface.Storage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bupt.memes.util.TimeUtil.getCurrentHour;

/**
 * 定时任务
 */

@Service
@AllArgsConstructor
public class Schedule {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(Schedule.class);

    final Review review;

    final Storage storage;

    final ISubmission submission;

    final SysConfigService sysConfig;


    /**
     * 每隔 30min 自动发布一次
     */
    @Scheduled(fixedRate = 1000 * 1800)
    public void autoRelease() {
        int release = review.release();
        logger.info("auto release {} submissions", release);


        int toBeReviewed = review.listSubmissions().size();
        long reviewPassedNum = review.getReviewPassedNum();
        int targetNum = sysConfig.getSys().getMIN_SUBMISSIONS();

        boolean botShouldEnabled = shouldBotEnabled(reviewPassedNum, toBeReviewed, targetNum);

        // 九点之后开启机器人，爬虫会在10点开始爬取
        int currentHour = getCurrentHour();
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
    public void cleanDeletedSub() {
        logger.info("clean submissions start.");
        List<Submission> deletedSubmission = submission.getDeletedSubmission();
        Iterator<Submission> iterator = deletedSubmission.iterator();
        while (iterator.hasNext()) {
            Submission next = iterator.next();
            if (next.textFormat()) {
                submission.hardDeleteSubmission(next.getId());
                iterator.remove();
            }
        }

        if (deletedSubmission.isEmpty()) {
            logger.info("clean submissions done, no images to be deleted from storage.");
            return;
        }

        logger.info("clean images start, {} images to be deleted from storage.", deletedSubmission.size());

        List<String> keyList = new ArrayList<>();
        Map<String, String> nameIdMap = new HashMap<>();
        for (Submission sub : deletedSubmission) {
            keyList.add(sub.getName());
            nameIdMap.put(sub.getName(), sub.getId());
        }

        String[] array = keyList.toArray(new String[0]);
        var nameStatusMap = storage.delete(array);

        if (nameStatusMap == null) {
            logger.error("clean images failed,because of storage error.");
            return;
        }

        nameStatusMap.forEach((objName, status) -> {
            if (status) {
                submission.hardDeleteSubmission(nameIdMap.get(objName));
            }
        });
        logger.info("clean  done, {} images deleted from storage.", nameStatusMap.entrySet().stream().filter(Map.Entry::getValue).count());
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void SyncTopStatus() {
        logger.info("SyncTopStatus start.");
        sysConfig.updateTopSubmission();
        logger.info("SyncTopStatus done.");
    }

}

package com.bupt.memes.service;

import com.bupt.memes.model.common.LogDocument;
import com.bupt.memes.model.media.News;
import com.bupt.memes.model.media.Submission;
import com.bupt.memes.model.rss.RSSItem;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.service.Interface.Review;
import com.bupt.memes.service.Interface.Storage;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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

    final MongoTemplate mongoTemplate;

    final MeterRegistry registry;

    private final static Map<String, AtomicLong> map = new HashMap<>();

    /**
     * 每隔 30min 自动开启或关闭机器人
     */
    @Scheduled(fixedRate = 1000 * 1800)
    public void autobots() {

        long waitingNum = review.getWaitingNum();
        long passedNum = review.getPassedNum();
        int targetNum = sysConfig.getSys().getMIN_SUBMISSIONS();

        boolean notMeetsMinReq = notMeetsMinReq(passedNum, waitingNum, targetNum);
        int currentHour = getCurrentHour();
        if (notMeetsMinReq || (currentHour >= 21 || currentHour <= 8)) {
            sysConfig.enableBot();
            logger.info("enable bot notMeetsMinReq: {}  currentHour: {}", notMeetsMinReq, currentHour);
            return;
        }
        // 机器人关闭
        sysConfig.disableBot();
    }

    /**
     * 通过 meme 的数量判断机器人是否应该开启
     *
     * @param passedNum  当前已审核通过的数量
     * @param waitingNum 待审核的数量
     * @param targetNum  目标数量
     * @return 是否应该开启
     */
    public static boolean notMeetsMinReq(long passedNum, long waitingNum, int targetNum) {
        // 如果当前已发布的数量大于目标数量，直接返回 false
        if (passedNum >= targetNum) {
            logger.info("bot should disable because of passedNum: {} more than targetNum: {}", passedNum, targetNum);
            return false;
        }
        int needed = (int) (targetNum - passedNum);

        // 审核的通过率大概是 2/3，所以需要的数量是 1.5 倍
        boolean b = waitingNum < (needed * 1.5);
        logger.info("bot should {} because of waitingNum: {} needed: {}", b ? "enable" : "disable", waitingNum, needed);
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
        logger.info("clean  done, {} images deleted from storage.",
                nameStatusMap.entrySet().stream().filter(Map.Entry::getValue).count());
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void SyncTopStatus() {
        logger.info("SyncTopStatus start.");
        sysConfig.updateTopSubmission();
        logger.info("SyncTopStatus done.");
    }

    @Scheduled(fixedRate = 1000 * 30)
    private void scanDBStatus() {

        long submissionCount = mongoTemplate.count(new Query(), "submission");
        map.computeIfAbsent("submission.count",
                _ -> registry.gauge("submission.count", new AtomicLong(submissionCount))).set(submissionCount);

        long logCount = mongoTemplate.count(new Query(), LogDocument.class);
        map.computeIfAbsent("log.count", _ -> registry.gauge("log.count", new AtomicLong(logCount))).set(logCount);

        long rssCount = mongoTemplate.count(new Query(), RSSItem.class);
        map.computeIfAbsent("rss.count", _ -> registry.gauge("rss.count", new AtomicLong(rssCount))).set(rssCount);

        long newsCount = mongoTemplate.count(new Query(), News.class);
        map.computeIfAbsent("news.count", _ -> registry.gauge("news.count", new AtomicLong(newsCount))).set(newsCount);

        logger.info("ScanDBStatus done, submissionCount: {}, logCount: {}, rssCount: {}, newsCount: {}",
                submissionCount, logCount, rssCount, newsCount);

    }

}

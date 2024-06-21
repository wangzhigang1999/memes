package com.bupt.memes.cron;

import com.bupt.memes.model.Sys;
import com.bupt.memes.model.common.LogDocument;
import com.bupt.memes.model.media.News;
import com.bupt.memes.model.media.Submission;
import com.bupt.memes.model.rss.RSSItem;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.service.Interface.Review;
import com.bupt.memes.service.Interface.Storage;
import com.bupt.memes.service.SysConfigService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
public class CronJob {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(CronJob.class);

    final Review review;

    final Storage storage;

    final ISubmission submissionService;

    final SysConfigService sysConfig;

    final MongoTemplate mongoTemplate;

    final MeterRegistry registry;

    private final static Map<String, AtomicLong> map = new HashMap<>();

    /**
     * 每隔 30min 自动开启或关闭机器人
     * 逻辑如下：
     * 1. 如果已审核通过的 meme 数量大于等于目标数量，直接关闭机器人
     * 2. 如果审核通过的 meme 数量小于目标数量，且待审核的 meme 数量小于需要的 meme 数量的 1.5 倍，开启机器人
     * 3. 如果当前时间在 21 点到 8 点之间，开启机器人
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
        sysConfig.disableBot();
    }

    /**
     * 每天一次，清理一次存储，删除已经删除的 submission
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanDeletedSub() {
        logger.info("cleanDeletedSub start.");
        List<Submission> deletedSubmission = submissionService.getDeletedSubmission();
        Iterator<Submission> iterator = deletedSubmission.iterator();
        logger.info("cleanDeletedSub, deletedSubmission size: {}", deletedSubmission.size());
        // 删除 textFormat 的 submission
        while (iterator.hasNext()) {
            Submission next = iterator.next();
            if (next.textFormat()) {
                submissionService.hardDeleteSubmission(next.getId());
                iterator.remove();
                logger.info("clean textFormat submission, id: {}", next.getId());
            }
        }

        if (deletedSubmission.isEmpty()) {
            logger.info("clean submissions done, no images to be deleted from storage.");
            return;
        }

        List<String> keyList = new ArrayList<>();
        Map<String, String> nameIdMap = new HashMap<>();
        for (Submission sub : deletedSubmission) {
            keyList.add(sub.getName());
            nameIdMap.put(sub.getName(), sub.getId());
        }

        String[] array = keyList.toArray(new String[0]);
        var nameStatusMap = storage.delete(array);

        if (nameStatusMap == null) {
            logger.error("clean images task failed,because of storage error.");
            return;
        }

        nameStatusMap.forEach((objName, success) -> {
            if (success) {
                submissionService.hardDeleteSubmission(nameIdMap.get(objName));
                logger.info("clean image done, name: {}", objName);
            } else {
                logger.error("clean image failed, name: {}", objName);
            }
        });

    }

    @Scheduled(fixedRate = 1000 * 5)
    public void syncSys() {
        logger.debug("Periodic sync sys done.");
        sysConfig.setSys(mongoTemplate.findById("sys", Sys.class));
        logger.debug("Periodic sync top submission done.");
    }

    // 每分钟同步一次 top submission
    @Scheduled(fixedRate = 1000 * 60)
    public void syncTopSubmission() {
        logger.debug("Periodic sync top submission start.");
        sysConfig.syncTopSubmission();
        logger.debug("Periodic sync top submission done.");
    }

    /**
     * 每隔 5min 扫描一次数据库状态，用于监控
     */
    @Scheduled(fixedRate = 1000 * 60 * 5)
    private void scanDBStatus() {

        long submissionCount = mongoTemplate.count(new Query(), "submission");
        Objects.requireNonNull(map.computeIfAbsent("submission.count", k -> registry.gauge(k, new AtomicLong(submissionCount))))
                .set(submissionCount);
        long logCount = mongoTemplate.count(new Query(), LogDocument.class);
        Objects.requireNonNull(map.computeIfAbsent("log.count", k -> registry.gauge(k, new AtomicLong(logCount))))
                .set(logCount);
        long rssCount = mongoTemplate.count(new Query(), RSSItem.class);
        Objects.requireNonNull(map.computeIfAbsent("rss.count", k -> registry.gauge(k, new AtomicLong(rssCount))))
                .set(rssCount);
        long newsCount = mongoTemplate.count(new Query(), News.class);
        Objects.requireNonNull(map.computeIfAbsent("news.count", k -> registry.gauge(k, new AtomicLong(newsCount))))
                .set(newsCount);
        logger.debug("ScanDBStatus done, submissionCount: {}, logCount: {}, rssCount: {}, newsCount: {}",
                submissionCount, logCount, rssCount, newsCount);

    }

    private static boolean notMeetsMinReq(long passedNum, long waitingNum, int targetNum) {
        if (passedNum >= targetNum) {
            return false;
        }
        int needed = (int) (targetNum - passedNum);
        // 审核的通过率大概是 2/3，所以需要的数量是 1.5 倍
        return waitingNum < (needed * 1.5);
    }

}

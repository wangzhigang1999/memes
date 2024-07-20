package com.memes.schedule;

import com.memes.config.AppConfig;
import com.memes.model.common.LogDocument;
import com.memes.model.news.News;
import com.memes.model.rss.RSSItem;
import com.memes.model.submission.Submission;
import com.memes.model.submission.SubmissionGroup;
import com.memes.model.submission.SubmissionType;
import com.memes.service.review.ReviewService;
import com.memes.service.storage.StorageService;
import com.memes.service.submission.SubmissionService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.memes.model.common.SubmissionCollection.PASSED_SUBMISSION;
import static com.memes.model.common.SubmissionCollection.WAITING_SUBMISSION;
import static com.memes.util.TimeUtil.getCurrentHour;

/**
 * 定时任务
 */

@Service
@AllArgsConstructor
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
@Slf4j
public class SubmissionTasks {

    final ReviewService reviewService;

    final StorageService storageService;

    final SubmissionService submissionService;

    final AppConfig appConfig;

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

        long waitingNum = reviewService.getStatusNum(WAITING_SUBMISSION);
        long passedNum = reviewService.getStatusNum(PASSED_SUBMISSION);
        int targetNum = appConfig.minSubmissions;

        boolean notMeetsMinReq = notMeetsMinReq(passedNum, waitingNum, targetNum);
        int currentHour = getCurrentHour();
        if (notMeetsMinReq || (currentHour >= 21 || currentHour <= 8)) {
            log.info("enable bot notMeetsMinReq: {}  currentHour: {}", notMeetsMinReq, currentHour);
            appConfig.setBotUp(true);
            return;
        }
        appConfig.setBotUp(false);
    }

    /**
     * 每天一次，清理一次存储，删除已经删除的 submission
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanDeletedSub() {
        log.info("cleanDeletedSub start.");
        var deletedSubmission = submissionService.getDeletedSubmission();
        var iterator = deletedSubmission.iterator();
        log.info("cleanDeletedSub, deletedSubmission size: {}", deletedSubmission.size());
        // 删除 textFormat 的 submission
        while (iterator.hasNext()) {
            var next = iterator.next();
            if (next.textFormat()) {
                submissionService.hardDelete(next.getId());
                iterator.remove();
                log.info("clean textFormat submission, id: {}", next.getId());
            }
        }

        if (deletedSubmission.isEmpty()) {
            log.info("clean submissions done, no images to be deleted from storage.");
            return;
        }

        List<String> keyList = new ArrayList<>();
        Map<String, String> nameIdMap = new HashMap<>();
        for (Submission sub : deletedSubmission) {
            if (Objects.requireNonNull(sub.getSubmissionType()) == SubmissionType.BATCH) {
                for (Submission child : ((SubmissionGroup) sub).getChildren()) {
                    keyList.add(child.getName());
                    nameIdMap.put(child.getName(), child.getId());
                }
            } else {
                keyList.add(sub.getName());
                nameIdMap.put(sub.getName(), sub.getId());
            }
        }

        String[] array = keyList.toArray(new String[0]);
        var nameStatusMap = storageService.delete(array);

        if (nameStatusMap == null) {
            log.error("clean images task failed,because of storage error.");
            return;
        }

        nameStatusMap.forEach((objName, success) -> {
            if (success) {
                submissionService.hardDelete(nameIdMap.get(objName));
                log.info("clean image done, name: {}", objName);
            } else {
                log.error("clean image failed, name: {}", objName);
            }
        });

    }

    // 每分钟同步一次 top submission
    @Scheduled(fixedRate = 1000 * 60)
    public void syncTopSubmission() {
        Set<Submission> oldSubmission = appConfig.topSubmissions;
        Set<Submission> newSubmission = new HashSet<>();
        oldSubmission.forEach(oldFromTop -> {
            Submission newFromDB = mongoTemplate.findById(oldFromTop.getId(), Submission.class);
            newSubmission.add(newFromDB);
        });
        appConfig.setTopSubmissions(newSubmission);
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
        log.debug("ScanDBStatus done, submissionCount: {}, logCount: {}, rssCount: {}, newsCount: {}",
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

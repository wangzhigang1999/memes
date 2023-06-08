package com.bupt.dailyhaha.mapper.mysql;

import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.util.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class MSubmissionTest {

    @Autowired
    MSubmission submission;

    /**
     * get random timestamp of a date
     *
     * @param date date in the format of YYYY-MM-DD
     * @return random timestamp of a date
     */
    private long getRandomTimestampOfDate(String date) {
        long start = Utils.getStartEpochMilli(date);
        var end = Utils.getStartEpochMilli(date) + 24 * 60 * 60 * 1000;
        return start + (long) (Math.random() * (end - start));
    }

    @Test
    void create() {

        File file = new File("memes");
        // get every name in the folder
        String[] list = file.list();
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.length; i++) {
            list[i] = "http://100.68.68.47:8080/" + list[i];
        }

        int length = list.length;
        String ymd = Utils.getYMD();
        long l = 10_000_000;
        Random random = new Random();

        // find the days from today to 2023-01-01


        var date = Instant.parse("2023-01-01T00:00:00.00Z").atZone(ZoneId.of("Asia/Shanghai")).toLocalDate();
        var today = Instant.now().atZone(java.time.ZoneId.of("Asia/Shanghai")).toLocalDate();
        var days = today.toEpochDay() - date.toEpochDay();


        CountDownLatch countDownLatch;
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(32, 64, 10, TimeUnit.HOURS, new LinkedBlockingQueue<>());
        countDownLatch = new CountDownLatch((int) (days * 10000));
        while (!date.toString().equals(Utils.getYMD())) {
            var finalDate = date;
            for (int i = 0; i < 10000; i++) {
                poolExecutor.execute(() -> {
                    Submission sub = new Submission();
                    sub.setDate(finalDate.toString());
                    sub.setHash(random.nextInt());
                    sub.setUrl(list[random.nextInt(length)]);
                    sub.setName(UUID.randomUUID().toString());
                    sub.setSubmissionType("IMAGE");
                    sub.setTimestamp(getRandomTimestampOfDate(finalDate.toString()));
                    sub.setReviewed(true);
                    sub.setDeleted(false);
                    sub.setUp(random.nextInt(5000));
                    sub.setDown(random.nextInt(5000));
                    submission.insert(sub);
                    countDownLatch.countDown();
                });
            }
            date = date.plusDays(1);
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
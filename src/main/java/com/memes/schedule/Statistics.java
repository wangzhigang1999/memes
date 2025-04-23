package com.memes.schedule;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.memes.mapper.MediaMapper;
import com.memes.mapper.RequestLogMapper;
import com.memes.mapper.SubmissionMapper;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Lazy(value = false)
public class Statistics {

    final MediaMapper mediaMapper;

    final RequestLogMapper requestLogMapper;

    final SubmissionMapper submissionMapper;

    final MeterRegistry registry;

    private final AtomicLong mediaCount = new AtomicLong(0);
    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong submissionCount = new AtomicLong(0);

    public Statistics(MediaMapper mediaMapper, RequestLogMapper requestLogMapper, SubmissionMapper submissionMapper, MeterRegistry registry) {
        this.mediaMapper = mediaMapper;
        this.requestLogMapper = requestLogMapper;
        this.submissionMapper = submissionMapper;
        this.registry = registry;

        // 注册 Gauge，只注册一次
        registry.gauge("memes.media.count", mediaCount);
        registry.gauge("memes.log.count", requestCount);
        registry.gauge("memes.submission.count", submissionCount);
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void statisticsSchedule() {
        try {
            log.debug("Starting statistics collection");

            // 更新 Gauge 的值
            mediaCount.set(this.mediaMapper.selectCount(null));
            requestCount.set(this.requestLogMapper.selectCount(null));
            submissionCount.set(this.submissionMapper.selectCount(null));

            log
                .info(
                    "Statistics collection completed - media: {}, requests: {}, submissions: {}",
                    mediaCount.get(),
                    requestCount.get(),
                    submissionCount.get());
        } catch (Exception e) {
            log.error("Failed to collect statistics", e);
        }
    }
}

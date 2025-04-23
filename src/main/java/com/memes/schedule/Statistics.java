package com.memes.schedule;

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

    public Statistics(MediaMapper mediaMapper, RequestLogMapper requestLogMapper, SubmissionMapper submissionMapper, MeterRegistry registry) {
        this.mediaMapper = mediaMapper;
        this.requestLogMapper = requestLogMapper;
        this.submissionMapper = submissionMapper;
        this.registry = registry;
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void statisticsSchedule() {
        try {
            log.debug("Starting statistics collection");

            Long mediaCount = this.mediaMapper.selectCount(null);
            Long requestCount = this.requestLogMapper.selectCount(null);
            Long submissionCount = this.submissionMapper.selectCount(null);

            registry.gauge("memes.media.count", mediaCount);
            registry.gauge("memes.log.count", requestCount);
            registry.gauge("memes.submission.count", submissionCount);

            log
                .debug(
                    "Statistics collection completed - media: {}, requests: {}, submissions: {}",
                    mediaCount,
                    requestCount,
                    submissionCount);
        } catch (Exception e) {
            registry.counter("memes.statistics.error.count").increment();
            log.error("Failed to collect statistics", e);
        }
    }
}

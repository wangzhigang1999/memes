package com.bupt.memes.service;

import com.bupt.memes.model.Sys;
import com.bupt.memes.model.media.Submission;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Data
@Lazy(value = false)
public class SysConfigService {
    private final MongoTemplate mongoTemplate;
    private Sys sys;

    final static Logger logger = LoggerFactory.getLogger(SysConfigService.class);

    public SysConfigService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        init();
    }

    private void init() {
        sys = mongoTemplate.findById("sys", Sys.class);
        sys = sys == null ? new Sys() : sys;

        sys.setMIN_SUBMISSIONS(setIfInvalid(sys.getMIN_SUBMISSIONS(), 50));
        sys.setTopK(setIfInvalid(sys.getTopK(), 10));
        sys.setSubmissionCacheSize(setIfInvalid(sys.getSubmissionCacheSize(), 1000));
        sys.setTopSubmission(sys.getTopSubmission() == null ? new HashSet<>() : sys.getTopSubmission());

        saveSys();
    }

    private int setIfInvalid(int value, int defaultValue) {
        return value <= 0 ? defaultValue : value;
    }

    private void saveSys() {
        mongoTemplate.save(sys);
    }

    public Boolean botUp() {
        return sys.getBotUp();
    }

    public Set<Submission> getTopSubmission() {
        return sys.getTopSubmission();
    }

    public Boolean disableBot() {
        if (!sys.getBotUp()) {
            return true;
        }
        sys.setBotUp(false);
        saveSys();
        return !sys.getBotUp();
    }

    public Boolean enableBot() {
        if (sys.getBotUp()) {
            return true;
        }
        sys.setBotUp(true);
        saveSys();
        return sys.getBotUp();
    }

    public boolean addTop(String id) {
        Submission submission = mongoTemplate.findById(id, Submission.class);
        if (submission == null) {
            return false;
        }
        sys.getTopSubmission().add(submission);
        saveSys();
        logger.info("add top submission " + id);
        return true;
    }

    public boolean removeTop(String id) {
        sys.getTopSubmission().removeIf(s -> Objects.equals(s.getId(), id));
        saveSys();
        logger.info("remove top submission " + id);
        return sys.getTopSubmission().stream().noneMatch(s -> Objects.equals(s.getId(), id));
    }

    public boolean setMinSubmissions(int minSubmissions) {
        if (minSubmissions < 0) {
            return false;
        }
        sys.setMIN_SUBMISSIONS(minSubmissions);
        saveSys();
        logger.info("set minSubmissions to " + minSubmissions);
        return true;
    }

    public void syncTopSubmission() {
        Set<Submission> oldSubmission = sys.getTopSubmission();
        Set<Submission> newSubmission = new HashSet<>();
        oldSubmission.forEach(oldFromTop -> {
            Submission newFromDB = mongoTemplate.findById(oldFromTop.getId(), Submission.class);
            newSubmission.add(newFromDB);
        });
        sys.setTopSubmission(newSubmission);
        saveSys();
    }

    public boolean setTopK(int topK) {
        if (topK < 0) {
            return false;
        }
        sys.setTopK(topK);
        saveSys();
        logger.info("set topK to " + topK);
        return true;
    }

    public int getTopK() {
        return sys.getTopK();
    }

    // get cache size
    public int getSubmissionCacheSize() {
        return sys.getSubmissionCacheSize();
    }

    public Boolean setCacheSize(int cacheSize) {
        if (cacheSize < 0) {
            return false;
        }
        sys.setSubmissionCacheSize(cacheSize);
        saveSys();
        logger.info("set cache size to " + cacheSize);
        return true;
    }
}

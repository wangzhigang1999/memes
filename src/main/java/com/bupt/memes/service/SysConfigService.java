package com.bupt.memes.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bupt.memes.model.Sys;
import com.bupt.memes.model.media.Submission;

@Service
@SuppressWarnings("null")
public class SysConfigService {
    final MongoTemplate mongoTemplate;
    private static Sys sys;

    public SysConfigService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        init();
    }

    public static Boolean getBotStatus() {
        return sys.getBotUp();
    }

    public synchronized static void setSys(Sys sys) {
        SysConfigService.sys = sys;
    }

    public static Set<Submission> getTop() {
        return sys.getTopSubmission();
    }

    /**
     * 初始化系统配置，如果有配置项不存在的，写一个默认值进去
     * 最后保存回 mongo
     * 如果有多个实例启动，会导致多次初始化，但是不会有问题，因为只有不存在的时候才会写入默认值，而且多个实例写入的默认值是一样的
     */
    private synchronized void init() {
        sys = mongoTemplate.findById("sys", Sys.class);
        if (sys == null) {
            sys = new Sys();
        }

        if (sys.getMIN_SUBMISSIONS() == 0) {
            sys.setMIN_SUBMISSIONS(50);
        }

        mongoTemplate.save(sys);
    }

    public synchronized Boolean disableBot() {
        if (!sys.getBotUp()) {
            return true;
        }
        sys.setBotUp(false);
        Sys target = mongoTemplate.save(sys);
        return !target.getBotUp();
    }

    public synchronized Boolean enableBot() {
        if (sys.getBotUp()) {
            return true;
        }
        sys.setBotUp(true);
        Sys target = mongoTemplate.save(sys);
        return target.getBotUp();
    }

    public synchronized boolean addTop(String id) {
        Submission submission = mongoTemplate.findById(id, Submission.class);
        if (submission == null) {
            return false;
        }
        sys.getTopSubmission().add(submission);
        mongoTemplate.save(sys);
        return true;
    }

    public synchronized boolean removeTop(String id) {
        sys.getTopSubmission().removeIf(s -> Objects.equals(s.getId(), id));
        Sys save = mongoTemplate.save(sys);
        return save.getTopSubmission().stream().noneMatch(s -> Objects.equals(s.getId(), id));
    }

    public synchronized boolean setMinSubmissions(int minSubmissions) {
        if (minSubmissions < 0) {
            return false;
        }
        sys.setMIN_SUBMISSIONS(minSubmissions);
        mongoTemplate.save(sys);
        return true;
    }

    /**
     * 更新置顶投稿
     * 置顶的投稿是在单独的集合中存储的，而对于投稿的修改是在另一个集合中，因此需要定时的更新置顶投稿的状态，如点赞等
     */
    @Transactional
    public synchronized void updateTopSubmission() {
        Set<Submission> oldSubmission = sys.getTopSubmission();
        Set<Submission> newSubmission = new HashSet<>();
        oldSubmission.forEach(oldFromTop -> {
            Submission newFromDB = mongoTemplate.findById(oldFromTop.getId(), Submission.class);
            newSubmission.add(newFromDB);
        });
        sys.setTopSubmission(newSubmission);
        mongoTemplate.save(sys);
    }

    public Sys getSys() {
        return sys;
    }
}

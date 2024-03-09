package com.bupt.memes.service;

import com.bupt.memes.model.Sys;
import com.bupt.memes.model.media.Submission;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

@Service
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

    public static void setSys(Sys sys) {
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
    private void init() {
        sys = mongoTemplate.findById("sys", Sys.class);
        if (sys == null) {
            sys = new Sys();
        }

        if (sys.getMIN_SUBMISSIONS() == 0) {
            sys.setMIN_SUBMISSIONS(50);
        }

        mongoTemplate.save(sys);
    }

    public Boolean disableBot() {
        if (!sys.getBotUp()) {
            return true;
        }
        sys.setBotUp(false);
        Sys target = mongoTemplate.save(sys);
        return !target.getBotUp();
    }

    public Boolean enableBot() {
        if (sys.getBotUp()) {
            return true;
        }
        sys.setBotUp(true);
        Sys target = mongoTemplate.save(sys);
        return target.getBotUp();
    }

    public boolean addTop(String id) {
        Submission submission = mongoTemplate.findById(id, Submission.class);
        if (submission == null) {
            return false;
        }
        sys.getTopSubmission().add(submission);
        mongoTemplate.save(sys);
        return true;
    }

    public boolean removeTop(String id) {
        sys.getTopSubmission().removeIf(s -> Objects.equals(s.getId(), id));
        Sys save = mongoTemplate.save(sys);
        return save.getTopSubmission().stream().noneMatch(s -> Objects.equals(s.getId(), id));
    }

    public boolean setMinSubmissions(int minSubmissions) {
        if (minSubmissions < 0) {
            return false;
        }
        sys.setMIN_SUBMISSIONS(minSubmissions);
        mongoTemplate.save(sys);
        return true;
    }

    @Transactional
    public void updateTopSubmission() {
        Set<Submission> submission = sys.getTopSubmission();
        // find and replace
        submission.forEach(s -> {
            if (s == null)
                return;

            Submission byId = mongoTemplate.findById(s.getId(), Submission.class);
            assert byId != null;
            submission.remove(s);
            submission.add(byId);
        });
        sys.setTopSubmission(submission);
        mongoTemplate.save(sys);
    }

    public Sys getSys() {
        return sys;
    }
}

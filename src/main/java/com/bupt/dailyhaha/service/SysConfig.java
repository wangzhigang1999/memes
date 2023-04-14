package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.pojo.Sys;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SysConfig {
    final MongoTemplate mongoTemplate;
    final ApplicationContext applicationContext;

    public Sys sys;

    public SysConfig(MongoTemplate mongoTemplate, ApplicationContext applicationContext) {
        this.mongoTemplate = mongoTemplate;
        this.applicationContext = applicationContext;
        init();
    }


    private void init() {
        sys = mongoTemplate.findById("sys", Sys.class);
        if (sys == null) {
            sys = new Sys();
        }

        Map<String, ReleaseStrategy> impls = getImplsOfInterface(applicationContext);
        Set<String> releaseStrategy = new HashSet<>(impls.keySet());
        sys.setReleaseStrategy(releaseStrategy);

        if (sys.getSelectedReleaseStrategy() == null) {
            sys.setSelectedReleaseStrategy("default");
        }

        mongoTemplate.save(sys);
    }

    public Boolean disableBot() {
        if (!sys.getBotUp()) {
            return true;
        }
        sys.setBotUp(false);
        mongoTemplate.save(sys);
        return true;
    }

    public Boolean enableBot() {
        if (sys.getBotUp()) {
            return true;
        }

        sys.setBotUp(true);
        mongoTemplate.save(sys);
        return true;
    }

    public boolean botStatus() {
        return sys.getBotUp();
    }

    public boolean addTop(int hashcode) {
        Submission submission = mongoTemplate.findOne(Query.query(Criteria.where("hash").is(hashcode)), Submission.class);
        if (submission == null) {
            return false;
        }
        sys.getTopSubmission().add(submission);
        mongoTemplate.save(sys);
        return true;
    }

    public boolean removeTop(int hashcode) {
        var submission = new Submission(hashcode);
        sys.getTopSubmission().remove(submission);
        mongoTemplate.save(sys);
        return true;
    }

    public Set<Submission> getTop() {
        return sys.getTopSubmission();
    }

    public Set<String> getReleaseStrategy() {
        return sys.getReleaseStrategy();
    }

    public String getSelectedReleaseStrategy() {
        return sys.getSelectedReleaseStrategy();
    }

    public boolean setReleaseStrategy(String strategy) {
        if (sys.getReleaseStrategy().contains(strategy)) {
            sys.setSelectedReleaseStrategy(strategy);
            mongoTemplate.save(sys);
            return true;
        }
        return false;
    }

    public int getMaxSubmissions() {
        return sys.getMAX_SUBMISSIONS();
    }

    public boolean setMaxSubmissions(int maxSubmissions) {
        if (maxSubmissions < 0) {
            return false;
        }
        sys.setMAX_SUBMISSIONS(maxSubmissions);
        mongoTemplate.save(sys);
        return true;
    }


    private Map<String, ReleaseStrategy> getImplsOfInterface(ApplicationContext applicationContext) {
        return applicationContext.getBeansOfType(ReleaseStrategy.class);
    }
}

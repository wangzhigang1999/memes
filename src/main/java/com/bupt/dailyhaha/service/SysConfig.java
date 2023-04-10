package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.pojo.Sys;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysConfig {
    final MongoTemplate mongoTemplate;

    Sys sys;

    public SysConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.sys = mongoTemplate.findById("sys", Sys.class);
        if (this.sys == null) {
            this.sys = new Sys();
            mongoTemplate.save(this.sys);
        }
        // check whether fields is null
        if (this.sys.getTopSubmission() == null) {
            this.sys.setTopSubmission(Set.of());
        }
        mongoTemplate.save(this.sys);
    }

    public Boolean disableBot() {
        this.sys.setBotUp(false);
        mongoTemplate.save(this.sys);
        return true;
    }

    public Boolean enableBot() {
        this.sys.setBotUp(true);
        mongoTemplate.save(this.sys);
        return true;
    }

    public boolean botStatus() {
        return this.sys.getBotUp();
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
        Submission submission = mongoTemplate.findOne(Query.query(Criteria.where("hash").is(hashcode)), Submission.class);
        if (submission == null) {
            return false;
        }
        sys.getTopSubmission().remove(submission);
        mongoTemplate.save(sys);
        return true;
    }

    public Set<Submission> getTop() {
        return sys.getTopSubmission();
    }
}

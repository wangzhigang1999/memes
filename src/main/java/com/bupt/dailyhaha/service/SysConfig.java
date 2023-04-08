package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Sys;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

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
}

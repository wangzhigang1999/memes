package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Sys;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.Interface.ReleaseStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SysConfigService {
    final MongoTemplate mongoTemplate;
    final ApplicationContext applicationContext;

    public static Sys sys;

    public SysConfigService(MongoTemplate mongoTemplate, ApplicationContext applicationContext) {
        this.mongoTemplate = mongoTemplate;
        this.applicationContext = applicationContext;
        init();
    }


    /**
     * 初始化系统配置，如果有配置项不存在的，写一个默认值进去
     * 最后保存回mongo
     * 如果有多个实例启动，会导致多次初始化，但是不会有问题，因为只有不存在的时候才会写入默认值，而且多个实例写入的默认值是一样的
     */
    private void init() {
        sys = mongoTemplate.findById("sys", Sys.class);
        if (sys == null) {
            sys = new Sys();
        }

        /*
            设置发布策略
            1. 扫描所有的实现了 ReleaseStrategy 接口的类
            2. 将类名作为策略名
            3. 设置一个策略名为 default 的策略，作为默认策略
         */
        Map<String, ReleaseStrategy> impls = getImplsOfInterface(applicationContext);
        Set<String> releaseStrategy = new HashSet<>(impls.keySet());
        sys.setReleaseStrategy(releaseStrategy);

        if (sys.getSelectedReleaseStrategy() == null) {
            sys.setSelectedReleaseStrategy("default");
        }

        if (sys.getMAX_HISTORY() == 0) {
            sys.setMAX_HISTORY(7);
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


    public boolean setReleaseStrategy(String strategy) {
        if (sys.getReleaseStrategy().contains(strategy)) {
            sys.setSelectedReleaseStrategy(strategy);
            mongoTemplate.save(sys);
            return true;
        }
        return false;
    }


    public boolean setMinSubmissions(int minSubmissions) {
        if (minSubmissions < 0) {
            return false;
        }
        sys.setMIN_SUBMISSIONS(minSubmissions);
        mongoTemplate.save(sys);
        return true;
    }


    public boolean setMaxHistory(int maxHistory) {
        if (maxHistory < 0) {
            return false;
        }
        sys.setMAX_HISTORY(maxHistory);
        mongoTemplate.save(sys);
        return true;
    }


    public Sys getSys() {
        sys.getTopSubmission().clear();
        return sys;
    }


    /**
     * 获取所有实现了ReleaseStrategy接口的类
     *
     * @param applicationContext Spring上下文
     * @return 所有实现了ReleaseStrategy接口的类
     */
    private Map<String, ReleaseStrategy> getImplsOfInterface(ApplicationContext applicationContext) {
        return applicationContext.getBeansOfType(ReleaseStrategy.class);
    }
}

package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.mapper.MSubmission;
import com.bupt.dailyhaha.mapper.MSys;
import com.bupt.dailyhaha.pojo.SysConfig;
import com.bupt.dailyhaha.pojo.media.Submission;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SysConfigService {
    final ApplicationContext applicationContext;

    final MSubmission submissionMapper;

    public SysConfig sysConfig;

    final MSys sysMapper;

    public SysConfigService(ApplicationContext applicationContext, MSubmission submissionMapper, MSys sysMapper) {
        this.applicationContext = applicationContext;
        this.submissionMapper = submissionMapper;
        this.sysMapper = sysMapper;
        init();
    }


    /**
     * 初始化系统配置，如果有配置项不存在的，写一个默认值进去
     * 最后保存回mongo
     * 如果有多个实例启动，会导致多次初始化，但是不会有问题，因为只有不存在的时候才会写入默认值，而且多个实例写入的默认值是一样的
     */
    private void init() {
        sysConfig = sysMapper.load();
        if (sysConfig == null) {
            sysConfig = new SysConfig();
        }

        /*
            设置发布策略
            1. 扫描所有的实现了 ReleaseStrategy 接口的类
            2. 将类名作为策略名
            3. 设置一个策略名为 default 的策略，作为默认策略
         */
        Map<String, IReleaseStrategy> impls = getImplsOfInterface(applicationContext);
        Set<String> releaseStrategy = new HashSet<>(impls.keySet());
        sysConfig.setReleaseStrategy(releaseStrategy);

        if (sysConfig.getSelectedReleaseStrategy() == null) {
            sysConfig.setSelectedReleaseStrategy("default");
        }

        if (sysConfig.getMAX_HISTORY() == 0) {
            sysConfig.setMAX_HISTORY(7);
        }

        if (sysConfig.getMIN_SUBMISSIONS() == 0) {
            sysConfig.setMIN_SUBMISSIONS(50);
        }

        if (sysConfig.getTopSubmission() == null) {
            sysConfig.setTopSubmission(new HashSet<>());
        }

        sysMapper.save(sysConfig);
    }

    public Boolean disableBot() {
        if (!sysConfig.getBotUp()) {
            return true;
        }
        sysConfig.setBotUp(false);
        sysMapper.save(sysConfig);

        return true;
    }

    public Boolean enableBot() {
        if (sysConfig.getBotUp()) {
            return true;
        }

        sysConfig.setBotUp(true);
        sysMapper.save(sysConfig);

        return true;
    }

    public boolean botStatus() {
        return sysConfig.getBotUp();
    }

    public boolean addTop(int hashcode) {
        Submission submission = submissionMapper.findByHash(hashcode);
        if (submission == null) {
            return false;
        }
        sysConfig.getTopSubmission().add(submission);
        sysMapper.save(sysConfig);

        return true;
    }

    public boolean removeTop(int hashcode) {
        var submission = new Submission(hashcode);
        sysConfig.getTopSubmission().remove(submission);
        sysMapper.save(sysConfig);

        return true;
    }

    public Set<Submission> getTop() {
        return sysConfig.getTopSubmission();
    }

    public Set<String> getReleaseStrategy() {
        return sysConfig.getReleaseStrategy();
    }

    public String getSelectedReleaseStrategy() {
        return sysConfig.getSelectedReleaseStrategy();
    }

    public boolean setReleaseStrategy(String strategy) {
        if (sysConfig.getReleaseStrategy().contains(strategy)) {
            sysConfig.setSelectedReleaseStrategy(strategy);
            sysMapper.save(sysConfig);
            return true;
        }
        return false;
    }

    public int getMinSubmissions() {
        return sysConfig.getMIN_SUBMISSIONS();
    }

    public boolean setMinSubmissions(int minSubmissions) {
        if (minSubmissions < 0) {
            return false;
        }
        sysConfig.setMIN_SUBMISSIONS(minSubmissions);
        sysMapper.save(sysConfig);

        return true;
    }

    public int getMaxHistory() {
        return sysConfig.getMAX_HISTORY();
    }

    public boolean setMaxHistory(int maxHistory) {
        if (maxHistory < 0) {
            return false;
        }
        sysConfig.setMAX_HISTORY(maxHistory);
        sysMapper.save(sysConfig);

        return true;
    }


    /**
     * 获取所有实现了ReleaseStrategy接口的类
     *
     * @param applicationContext Spring上下文
     * @return 所有实现了ReleaseStrategy接口的类
     */
    private Map<String, IReleaseStrategy> getImplsOfInterface(ApplicationContext applicationContext) {
        return applicationContext.getBeansOfType(IReleaseStrategy.class);
    }
}

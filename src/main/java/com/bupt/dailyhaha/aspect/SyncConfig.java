package com.bupt.dailyhaha.aspect;

import com.bupt.dailyhaha.pojo.Sys;
import com.bupt.dailyhaha.service.SysConfigService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SyncConfig {

    final MongoTemplate template;

    Logger logger = org.slf4j.LoggerFactory.getLogger(SyncConfig.class);

    public SyncConfig(MongoTemplate template) {
        this.template = template;
    }

    // for all public methods in com.bupt.memes.service.SysConfigService
    @Pointcut("execution(public * com.bupt.dailyhaha.service.SysConfigService.*(..))")
    public void pubMethods() {
    }

    @Before("pubMethods()")
    public void beforePubMethods(JoinPoint joinPoint) {
        logger.info("Syncing sys config before {}", joinPoint.getSignature().getName());
        SysConfigService.sys = template.findById("sys", Sys.class);
    }
}

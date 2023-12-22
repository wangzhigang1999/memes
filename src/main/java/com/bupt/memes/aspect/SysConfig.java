package com.bupt.memes.aspect;

import com.bupt.memes.model.Sys;
import com.bupt.memes.service.SysConfigService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SysConfig {

    final MongoTemplate template;

    final Logger logger = org.slf4j.LoggerFactory.getLogger(SysConfig.class);

    public SysConfig(MongoTemplate template) {
        this.template = template;
    }

    // for all public methods in com.bupt.memes.service.SysConfigService
    @Pointcut("execution(public * com.bupt.memes.service.SysConfigService.*(..))")
    public void pubMethods() {
    }

    @Before("pubMethods()")
    public void beforePubMethods(JoinPoint joinPoint) {
        logger.info("Syncing sys config before {}", joinPoint.getSignature().getName());
        SysConfigService.setSys(template.findById("sys", Sys.class));
    }
}

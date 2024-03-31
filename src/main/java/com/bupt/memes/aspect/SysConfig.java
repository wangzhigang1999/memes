package com.bupt.memes.aspect;

import com.bupt.memes.model.Sys;
import com.bupt.memes.service.SysConfigService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SysConfig {

    final MongoTemplate template;

    public SysConfig(MongoTemplate template) {
        this.template = template;
    }

    @Pointcut("execution(public * com.bupt.memes.service.SysConfigService.*(..))")
    public void pubMethods() {
    }

    @Before("pubMethods()")
    public void beforePubMethods() {
        SysConfigService.setSys(template.findById("sys", Sys.class));
    }
}

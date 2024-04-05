package com.bupt.memes.aspect;

import com.bupt.memes.model.Sys;
import com.bupt.memes.service.SysConfigService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * 由于一些设计的问题，可能会导致前端获取到的 sys 配置不是最新的，这里通过 AOP 来解决这个问题
 * 在每次调用 SysConfigService 的方法之前，都会重新获取一次 sys 配置
 */
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

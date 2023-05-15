package com.bupt.dailyhaha.aspect;

import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.common.ReturnCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * @author wangz
 * 用来做权限校验，有一些接口只有管理员才能访问
 */
@Component
@Aspect
public class Auth {
    @Value("${token}")
    String localToken = UUID.randomUUID().toString();
    final Logger logger = LoggerFactory.getLogger(Auth.class);

    @Pointcut("@annotation(com.bupt.dailyhaha.anno.AuthRequired)")
    public void auth() {
    }


    @Around("auth()")
    public Object auth(ProceedingJoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        var request = attributes.getRequest();
        var token = request.getHeader("token");
        if (token == null || !token.equals(localToken)) {
            logger.warn("token is not correct");
            return ResultData.fail(ReturnCode.RC401);
        }
        return joinPoint.proceed();
    }


}

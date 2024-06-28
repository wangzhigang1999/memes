package com.memes.aspect;

import com.memes.exception.AppException;
import com.memes.util.Preconditions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * 用来做权限校验，有一些接口只有管理员才能访问
 */
@Component
@Aspect
@Lazy(false)
@Slf4j
public class Auth {
    @Value("${token}")
    String localToken = UUID.randomUUID().toString();
    @Value("${spring.profiles.active}")
    String activeProfile;

    @Pointcut("@annotation(com.memes.annotation.AuthRequired)")
    public void auth() {
    }

    /**
     * 校验 token
     */
    @Around("auth()")
    public Object auth(ProceedingJoinPoint joinPoint) throws Throwable {
        if ("dev".equals(activeProfile)) {
            return joinPoint.proceed();
        }
        /*
         * 从请求头中获取 token，如果 token 不正确，返回 401
         * 进程内部的 token，只有在启动时才会生成，如果进程重启，token 会改变；也可以通过环境变量传入
         */
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Preconditions.checkNotNull(attributes, AppException.unauthorized("Request is null"));
        var httpMethod = attributes.getRequest().getMethod();
        var path = "%s %s".formatted(httpMethod, attributes.getRequest().getRequestURI());
        var request = attributes.getRequest();
        var token = request.getHeader("token");
        Preconditions.checkArgument(token != null && token.equals(localToken), AppException.unauthorized(path));
        log.info("Authorized access: {}", path);
        return joinPoint.proceed();
    }

    @PostConstruct
    public void init() {
        log.info("Local token: {}", localToken);
    }

}

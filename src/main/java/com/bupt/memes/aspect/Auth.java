package com.bupt.memes.aspect;

import com.bupt.memes.model.common.ResultData;
import com.bupt.memes.model.common.ReturnCode;
import jakarta.servlet.http.HttpServletResponse;
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
 *         用来做权限校验，有一些接口只有管理员才能访问
 */
@Component
@Aspect
public class Auth {
    @Value("${token}")
    String localToken = UUID.randomUUID().toString();
    final Logger logger = LoggerFactory.getLogger(Auth.class);

    @Pointcut("@annotation(com.bupt.memes.anno.AuthRequired)")
    public void auth() {
    }

    /**
     * 校验 token
     */
    @Around("auth()")
    public Object auth(ProceedingJoinPoint joinPoint) throws Throwable {

        /*
         * 从请求头中获取 token，如果 token 不正确，返回 401
         * 进程内部的 token，只有在启动时才会生成，如果进程重启，token 会改变；也可以通过环境变量传入
         */
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        var request = attributes.getRequest();
        var token = request.getHeader("token");
        if (token == null || !token.equals(localToken)) {
            logger.warn("token is not correct");
            HttpServletResponse response = attributes.getResponse();
            if (response != null) {
                response.setStatus(401);
            }
            return ResultData.fail(ReturnCode.RC401);
        }
        return joinPoint.proceed();
    }

}

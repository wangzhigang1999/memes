package com.bupt.dailyhaha.aspect;

import com.bupt.dailyhaha.mapper.MLog;
import com.bupt.dailyhaha.pojo.common.LogDocument;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 记录请求日志
 * 将网关的信息保存到mongodb中，一方面统计调用，另一方面也可以用于排查问题
 */
@Component
@Aspect
public class Audit {
    final MLog logMapper;

    // todo 用虚拟线程完全的替代这个线程池
    ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 0, TimeUnit.HOURS, new LinkedBlockingQueue<>());

    Logger logger = org.slf4j.LoggerFactory.getLogger(Audit.class);

    public final static String instanceUUID = UUID.randomUUID().toString();


    public Audit(MLog mLog) {
        this.logMapper = mLog;
    }

    @Pointcut("execution(* com.bupt.dailyhaha.controller.*.*.*(..))")
    public void controller() {
    }


    @Around(value = "controller()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        var request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        // get uuid from headers
        var uuid = request.getHeader("uuid");

        logger.info("url: {}, method: {}, ip: {}, classMethod: {}", url, method, ip, classMethod);

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();

        HttpServletResponse response = attributes.getResponse();
        assert response != null;
        int status = response.getStatus();

        pool.submit(() -> {
            LogDocument document = new LogDocument();
            document.setUrl(url)
                    .setMethod(method)
                    .setIp(ip)
                    .setClassMethod(classMethod)
                    .setDetail(proceed.toString())
                    .setParameterMap(request.getParameterMap())
                    .setUuid((uuid == null || uuid.isEmpty()) ? "unknown" : uuid)
                    .setStatus(status)
                    .setTimecost(end - start)
                    .setTimestamp(start)
                    .setInstanceUUID(instanceUUID);
            logMapper.insertLog(document);
        });


        return proceed;
    }
}

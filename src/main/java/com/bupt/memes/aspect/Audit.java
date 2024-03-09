package com.bupt.memes.aspect;

import com.bupt.memes.model.common.LogDocument;
import com.mongodb.client.MongoClient;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 记录请求日志
 * 将网关的信息保存到 mongodb 中，一方面统计调用，另一方面也可以用于排查问题
 */
@Component
@Aspect
public class Audit {
    final MongoClient client;

    final MongoTemplate template;

    final MeterRegistry registry;

    /**
     * 用于在线程之内传递变量 UUID，是一个用户的标识
     */
    public final static ThreadLocal<String> threadLocalUUID = ThreadLocal.withInitial(() -> "anonymous");

    public final static ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();


    public final static String instanceUUID = UUID.randomUUID().toString();

    @Value("${spring.data.mongodb.database}")
    public String database;

    public final String env;

    private final ConcurrentHashMap<String, Timer> timerMap = new ConcurrentHashMap<>();


    public Audit(MongoTemplate template, MongoClient client, MeterRegistry registry) {
        this.client = client;
        this.template = template;
        this.registry = registry;
        env = System.getenv("env");
    }

    @Pointcut("execution(* com.bupt.memes.controller.*.*.*(..))")
    public void controller() {
    }


    @Around(value = "controller()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        @SuppressWarnings("null")
        var request = attributes.getRequest();
        var uuid = request.getHeader("uuid");
        var classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        if (uuid == null || uuid.isEmpty()) {
            uuid = "anonymous";
        }

        threadLocalUUID.set(uuid);
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();
        threadLocalUUID.remove();

        doAudit(request, classMethod, uuid, start, end);
        return proceed;
    }


    private void doAudit(final HttpServletRequest request, String classMethod, String uuid, long start, long end) {
        VIRTUAL_EXECUTOR.submit(() -> timerMap
                .computeIfAbsent(classMethod, this::getRequestTimer)
                .record(end - start, TimeUnit.MILLISECONDS));

        VIRTUAL_EXECUTOR.submit(() -> audit(request, uuid, start, end));
    }

    private void audit(HttpServletRequest request, String uuid, long start, long end) {
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        LogDocument document = new LogDocument();
        document.setUrl(url)
                .setMethod(method)
                .setParameterMap(request.getParameterMap())
                .setUuid((uuid == null || uuid.isEmpty()) ? "anonymous" : uuid)
                .setTimecost(end - start)
                .setTimestamp(start)
                .setInstanceUUID(instanceUUID);
        template.save(document);
    }

    private Timer getRequestTimer(String classMethod) {
        return Timer.builder("http_request_time")
                .description("http request time")
                .tags(Tags.of("class_method", classMethod))
                .register(registry);
    }
}

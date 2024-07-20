package com.memes.aspect;

import com.google.gson.Gson;
import com.memes.model.common.LogDocument;
import com.mongodb.client.MongoClient;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 记录请求日志
 * 将网关的信息保存到 mongodb 中，一方面统计调用，另一方面也可以用于排查问题
 */
@Component
@Aspect
@Slf4j
public class Audit {
    final MongoClient client;

    final MongoTemplate template;

    final MeterRegistry registry;

    /**
     * 用于在线程之内传递变量 UUID，是一个用户的标识
     */
    public final static ThreadLocal<String> threadLocalUUID = ThreadLocal.withInitial(() -> "anonymous");

    public final static ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    // 用于标识当前实例
    public final static String INSTANCE_UUID = "MEMES-%d-%s".formatted(System.currentTimeMillis(), UUID.randomUUID().toString().substring(0, 8));

    @Value("${spring.data.mongodb.database}")
    public String database;

    @Value("${spring.profiles.active}")
    public String env;

    private final ConcurrentHashMap<String, Timer> timerMap = new ConcurrentHashMap<>();

    public Audit(MongoTemplate template, MongoClient client, MeterRegistry registry) {
        this.client = client;
        this.template = template;
        this.registry = registry;
    }

    @Pointcut("execution(* com.memes.controller.*.*.*(..))")
    public void controller() {
    }

    @Around(value = "controller()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        var request = Objects.requireNonNull(attributes).getRequest();
        var uuid = request.getHeader("uuid");
        var classMethod = "%s.%s".formatted(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        var url = request.getRequestURL().toString();
        var method = request.getMethod();
        var parameterMap = request.getParameterMap();
        log.debug("audit: classMethod: {}, url: {}, method: {},parameterMap: {}", classMethod, url, method, new Gson().toJson(parameterMap));
        /*
         * 这里使用 ThreadLocal 传递 uuid，在向数据库中插入数据时，可能会使用这个 uuid
         */
        threadLocalUUID.set(uuid);
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();
        threadLocalUUID.remove();

        CompletableFuture.runAsync(() -> audit(classMethod, url, method, uuid, start, end, parameterMap), pool);
        return proceed;
    }

    private void audit(String classMethod, String url, String method, String uuid, long start, long end, Map<String, String[]> map) {
        try {
            LogDocument document = new LogDocument();
            document.setUrl(url)
                    .setMethod(method)
                    .setParameterMap(map)
                    .setUuid(StringUtils.isEmpty(uuid) ? "anonymous" : uuid)
                    .setTimecost(end - start)
                    .setTimestamp(start)
                    .setInstanceUUID(INSTANCE_UUID);
            template.save(document);
        } catch (Exception e) {
            log.error("audit error", e);
        } finally {
            timerMap.computeIfAbsent(classMethod, _ -> Timer.builder("http_request_time")
                    .description("http request time")
                    .tags(Tags.of("class_method", classMethod))
                    .register(registry))
                    .record(end - start, TimeUnit.MILLISECONDS);
        }
    }
}

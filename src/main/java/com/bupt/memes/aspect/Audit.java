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
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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

    public final static ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(Audit.class);

    // 用于标识当前实例
    public final static String instanceUUID = "MEMES-" + System.currentTimeMillis() + "-"
            + UUID.randomUUID().toString().substring(0, 8);

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
        @SuppressWarnings("null")
        var request = attributes.getRequest();
        var uuid = request.getHeader("uuid");
        var classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        if (uuid == null || uuid.isEmpty()) {
            uuid = "anonymous";
        }

        /*
         * 这里使用 ThreadLocal 传递 uuid，在向数据库中插入数据时，可能会使用这个 uuid
         */
        threadLocalUUID.set(uuid);
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();
        threadLocalUUID.remove();

        asyncAudit(request, classMethod, uuid, start, end);
        return proceed;
    }

    /**
     * 异步记录日志和监控打点
     */
    private void asyncAudit(final HttpServletRequest request, String classMethod, String uuid, long start, long end) {
        virtualExecutor.submit(() -> timerMap
                .computeIfAbsent(classMethod, this::getRequestTimer)
                .record(end - start, TimeUnit.MILLISECONDS));
        virtualExecutor.submit(() -> audit(request, uuid, start, end));
    }

    private void audit(HttpServletRequest request, String uuid, long start, long end) {
        try {
            String method = request.getMethod();
            String url = request.getRequestURL().toString();
            LogDocument document = new LogDocument();
            document.setUrl(url)
                    .setMethod(method)
                    .setParameterMap(request.getParameterMap())
                    .setUuid(uuid)
                    .setTimecost(end - start)
                    .setTimestamp(start)
                    .setInstanceUUID(instanceUUID);
            template.save(document);
        } catch (Exception e) {
            logger.error("audit error", e);
        }
    }

    private Timer getRequestTimer(String classMethod) {
        return Timer.builder("http_request_time")
                .description("http request time")
                .tags(Tags.of("class_method", classMethod))
                .register(registry);
    }
}

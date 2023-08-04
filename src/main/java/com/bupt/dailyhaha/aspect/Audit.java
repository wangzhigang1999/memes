package com.bupt.dailyhaha.aspect;

import com.bupt.dailyhaha.pojo.common.LogDocument;
import com.mongodb.client.MongoClient;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
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
public class Audit implements CommandLineRunner {
    final MongoClient client;

    final MongoTemplate template;
    public static ThreadLocal<String> threadLocalUUID = ThreadLocal.withInitial(() -> "anonymous");

    ThreadPoolExecutor pool = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 0, TimeUnit.HOURS, new LinkedBlockingQueue<>());

    public final static long instanceStartTime = System.currentTimeMillis();
    public final static String instanceUUID = UUID.randomUUID().toString();

    @Value("${spring.data.mongodb.database}")
    public String database;

    public String env;


    public Audit(MongoTemplate template, MongoClient client) {
        this.client = client;
        this.template = template;
        env = System.getenv("env");
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
        HttpServletResponse response = attributes.getResponse();
        assert response != null;

        threadLocalUUID.set(uuid);

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();

        pool.submit(() -> {
            LogDocument document = new LogDocument();
            document.setUrl(url)
                    .setMethod(method)
                    .setIp(ip)
                    .setClassMethod(classMethod)
                    .setParameterMap(request.getParameterMap())
                    .setUuid((uuid == null || uuid.isEmpty()) ? "unknown" : uuid)
                    .setTimecost(end - start)
                    .setTimestamp(start)
                    .setEnv(env)
                    .setInstanceUUID(instanceUUID);
            template.save(document);
        });

        threadLocalUUID.remove();
        return proceed;
    }

    @Override
    public void run(String... args) {
        /*
            记录启动时间，因为render不保证24小时运行;需要统计宕机的时间
         */
        Document document = new Document("startTimestamp", instanceStartTime);
        var str = Instant.ofEpochMilli(instanceStartTime).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();
        document.append("startAt", str).append("env", env).append("instanceUUID", instanceUUID);
        client.getDatabase(database).getCollection("up").insertOne(document);
    }

    @PreDestroy
    public void exit() {
        pool.shutdown();
        var instanceEndTime = System.currentTimeMillis();
        Document document = new Document("endTimestamp", instanceEndTime);
        var str = Instant.ofEpochMilli(instanceEndTime).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();
        document.append("endAt", str);

        var duration = System.currentTimeMillis() - instanceStartTime;
        // convert to hours
        duration /= 1000.0 * 60 * 60;
        document.append("alive", duration);

        client.getDatabase(database).getCollection("up").updateOne(
                new Document("instanceUUID", instanceUUID),
                new Document("$set", document)
        );
    }
}

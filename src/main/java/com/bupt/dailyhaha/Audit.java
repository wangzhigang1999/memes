package com.bupt.dailyhaha;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class Audit implements CommandLineRunner {
    final MongoClient client;

    ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 0, TimeUnit.HOURS, new LinkedBlockingQueue<>());

    public final static Gson gson = new Gson();
    public final static long start = System.currentTimeMillis();
    public final static String uuid = UUID.randomUUID().toString();

    public String env;


    public Audit(MongoClient client) {
        this.client = client;
        env = System.getenv("env");
    }

    @Pointcut("execution(* com.bupt.dailyhaha.controller.Controller.*(..))")
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

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();

        HttpServletResponse response = attributes.getResponse();
        assert response != null;
        int status = response.getStatus();
        pool.submit(() -> {
            Document document = new Document()
                    .append("url", url)
                    .append("method", method)
                    .append("ip", ip)
                    .append("classMethod", classMethod)
                    .append("detail", gson.toJson(proceed))
                    .append("parameterMap", gson.toJson(request.getParameterMap()))
                    .append("status", status)
                    .append("time", end - start)
                    .append("timeStamp", System.currentTimeMillis())
                    .append("env", env)
                    .append("uuid", uuid);
            client.getDatabase("shadiao").getCollection("audit").insertOne(document);
        });
        return proceed;
    }

    @Override
    public void run(String... args) {
        /*
            ???????????????????????????render?????????24????????????;???????????????????????????
         */
        Document document = new Document("startTimestamp", start);
        var str = Instant.ofEpochMilli(start).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();
        document.append("startAt", str).append("env", env).append("uuid", uuid);
        client.getDatabase("shadiao").getCollection("up").insertOne(document);
    }

    @PreDestroy
    public void exit() {
        pool.shutdown();
        Document document = new Document("endTimestamp", System.currentTimeMillis());
        var str = Instant.ofEpochMilli(start).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();
        document.append("endAt", str);

        var duration = System.currentTimeMillis() - start;
        // convert to hours
        duration /= 1000.0 * 60 * 60;
        document.append("alive", duration);

        client.getDatabase("shadiao").getCollection("up").updateOne(
                new Document("uuid", uuid),
                new Document("$set", document)
        );
    }
}

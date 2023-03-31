package com.bupt.dailyhaha.aspect;

import com.google.gson.Gson;
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

    ThreadPoolExecutor pool = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 0, TimeUnit.HOURS, new LinkedBlockingQueue<>());

    public final static Gson gson = new Gson();
    public final static long start = System.currentTimeMillis();
    public final static String instanceUUID = UUID.randomUUID().toString();

    @Value("${spring.data.mongodb.database}")
    public String database;

    public String env;


    public Audit(MongoClient client) {
        this.client = client;
        env = System.getenv("env");
    }

    @Pointcut("execution(* com.bupt.dailyhaha.controller.*.*(..))")
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

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();

        // do not audit if uuid is null
        if (uuid == null || uuid.isEmpty()) {
            return proceed;
        }

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
                    .append("uuid", uuid)
                    .append("status", status)
                    .append("timeCost", end - start) // ms
                    .append("timeStamp", System.currentTimeMillis())
                    .append("env", env)
                    .append("instanceUUID", instanceUUID);
            client.getDatabase(database).getCollection("audit").insertOne(document);

            if (!env.equals("prod")) {
                System.out.println(document.toJson());
            }

        });
        return proceed;
    }

    @Override
    public void run(String... args) {
        /*
            记录启动时间，因为render不保证24小时运行;需要统计宕机的时间
         */
        Document document = new Document("startTimestamp", start);
        var str = Instant.ofEpochMilli(start).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();
        document.append("startAt", str).append("env", env).append("instanceUUID", instanceUUID);
        client.getDatabase(database).getCollection("up").insertOne(document);
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

        client.getDatabase(database).getCollection("up").updateOne(
                new Document("instanceUUID", instanceUUID),
                new Document("$set", document)
        );
    }
}

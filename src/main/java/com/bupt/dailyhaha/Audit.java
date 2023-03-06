package com.bupt.dailyhaha;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class Audit {
    final MongoClient client;

    ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 0, TimeUnit.HOURS, new LinkedBlockingQueue<>());

    public final static Gson gson = new Gson();


    public Audit(MongoClient client) {
        this.client = client;
    }

    @Pointcut("execution(* com.bupt.dailyhaha.Controller.*(..))")
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
                    .append("timeStamp", System.currentTimeMillis());
            client.getDatabase("shadiao").getCollection("audit").insertOne(document);

        });
        return proceed;
    }
}

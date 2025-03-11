package com.memes.aspect;

import com.memes.mapper.RequestLogMapper;
import com.memes.model.pojo.RequestLog;
import com.memes.util.GsonUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Aspect
@Component
public class Audit {
    private final MeterRegistry registry;
    private final RequestLogMapper requestLogMapper;
    private static final ThreadLocal<String> threadLocalUUID = ThreadLocal.withInitial(() -> "anonymous");
    private static final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
    private static final ConcurrentHashMap<String, Timer> timerMap = new ConcurrentHashMap<>();
    private static final String instanceUuid = UUID.randomUUID().toString();

    public Audit(MeterRegistry registry, RequestLogMapper requestLogMapper) {
        this.registry = registry;
        this.requestLogMapper = requestLogMapper;
    }

    @Pointcut("execution(* com.memes.controller..*.*(..))")
    public void controller() {
    }

    @Around("controller()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String uuid = request.getHeader("uuid");
        String classMethod = "%s.%s".formatted(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        log.debug("Audit: classMethod={}, url={}, method={}, parameters={}", classMethod, url, method, GsonUtil.toJson(parameterMap));

        threadLocalUUID.set(uuid);
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        threadLocalUUID.remove();

        CompletableFuture.runAsync(() -> saveRequestLog(classMethod, url, method, uuid, clientIp, userAgent, referer, start, end, parameterMap), pool);
        return result;
    }

    private void saveRequestLog(String classMethod, String url, String method, String uuid, String clientIp, String userAgent, String referer, long start,
        long end, Map<String, String[]> parameterMap) {
        try {
            RequestLog logEntry = RequestLog
                .builder()
                .url(url)
                .method(RequestLog.HttpMethod.valueOf(method.toUpperCase()))
                .ip(clientIp)
                .userAgent(userAgent)
                .refer(referer)
                .parameterMap(GsonUtil.toJson(parameterMap))
                .uuid(uuid)
                .timecost((int) (end - start))
                .timestamp(System.currentTimeMillis())
                .instanceUuid(instanceUuid)
                .createdAt(LocalDateTime.now())
                .build();

            requestLogMapper.insert(logEntry);
        } catch (Exception e) {
            log.error("Failed to save request log", e);
        } finally {
            timerMap
                .computeIfAbsent(
                    classMethod,
                    key -> Timer
                        .builder("http_request_time")
                        .description("HTTP request duration")
                        .tags(Tags.of("class_method", key))
                        .register(registry))
                .record(end - start, TimeUnit.MILLISECONDS);
        }
    }
}

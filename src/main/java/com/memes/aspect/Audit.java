package com.memes.aspect;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.memes.mapper.RequestLogMapper;
import com.memes.model.pojo.RequestLog;
import com.memes.util.GsonUtil;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class Audit {
    public static final String INSTANCE_UUID = UUID.randomUUID().toString();
    private static final String ANONYMOUS = "anonymous";
    private static final String UUID_HEADER = "uuid";
    private static final ConcurrentHashMap<String, Timer> TIMER_CACHE = new ConcurrentHashMap<>();

    private final MeterRegistry registry;
    private final RequestLogMapper requestLogMapper;
    private static final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    private static final ThreadLocal<String> THREAD_LOCAL_UUID = ThreadLocal.withInitial(() -> ANONYMOUS);

    public static String getCurrentUuid() {
        return THREAD_LOCAL_UUID.get();
    }

    @Pointcut("execution(* com.memes.controller..*.*(..))")
    public void controller() {
        // 切点定义
    }

    @Around("controller()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        RequestContext context = extractRequestContext(request, joinPoint);

        log
            .info(
                "Audit: classMethod={}, url={}, method={}, parameters={}",
                context.classMethod,
                context.url,
                context.method,
                GsonUtil.toJson(context.parameterMap));

        THREAD_LOCAL_UUID.set(context.uuid);
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            THREAD_LOCAL_UUID.remove();

            pool.execute(() -> saveRequestLog(context, startTime, duration));
        }
    }

    private RequestContext extractRequestContext(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        String classMethod = "%s.%s".formatted(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String uuid = request.getHeader(UUID_HEADER);

        return new RequestContext(classMethod, url, method, parameterMap, clientIp, userAgent, referer, uuid);
    }

    private void saveRequestLog(RequestContext context, long startTime, long duration) {
        try {
            RequestLog logEntry = RequestLog
                .builder()
                .url(context.url)
                .method(RequestLog.HttpMethod.valueOf(context.method.toUpperCase()))
                .ip(context.clientIp)
                .userAgent(context.userAgent)
                .refer(context.referer)
                .parameterMap(GsonUtil.toJson(context.parameterMap))
                .uuid(context.uuid)
                .timecost((int) duration)
                .timestamp(startTime)
                .instanceUuid(INSTANCE_UUID)
                .createdAt(LocalDateTime.now())
                .build();

            requestLogMapper.insert(logEntry);

            // 记录请求计时器指标
            getOrCreateTimer(context.classMethod).record(Duration.ofMillis(duration));
        } catch (Exception e) {
            log.error("Failed to save request log for {}: {}", context.url, e.getMessage(), e);
        }
    }

    private Timer getOrCreateTimer(String classMethod) {
        return TIMER_CACHE
            .computeIfAbsent(
                classMethod,
                key -> Timer
                    .builder("http_request_time")
                    .description("HTTP request duration")
                    .tags(Tags.of("class_method", key))
                    .register(registry));
    }

    // 内部类用于封装请求上下文，便于传递
    private record RequestContext(String classMethod, String url, String method, Map<String, String[]> parameterMap, String clientIp,
        String userAgent, String referer, String uuid) {
    }
}

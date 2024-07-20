package com.memes.aspect;

import com.google.common.util.concurrent.RateLimiter;
import com.memes.annotation.RateLimit;
import com.memes.exception.AppException;
import com.memes.util.Preconditions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@SuppressWarnings("UnstableApiUsage")
@Order(1)
public class RateLimitInterceptor {

    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> timestamps = new ConcurrentHashMap<>();
    private ScheduledExecutorService cleanerService;

    @Pointcut("@annotation(rateLimit)")
    public void limit(RateLimit rateLimit) {
    }

    @Before(value = "limit(rateLimit)", argNames = "joinPoint,rateLimit")
    public void limit(JoinPoint joinPoint, RateLimit rateLimit) {
        String key = joinPoint.getSignature().toString();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        var request = Objects.requireNonNull(attributes).getRequest();
        var uuid = request.getHeader("uuid");

        Preconditions.checkNotNull(uuid, AppException.invalidParam("UUID header is missing"));

        String userKey = String.format("%s-%s", key, uuid);
        long currentTime = System.currentTimeMillis();
        timestamps.put(userKey, currentTime);

        RateLimiter rateLimiter = limiters.computeIfAbsent(userKey, _ -> RateLimiter.create(rateLimit.value()));

        if (!rateLimiter.tryAcquire()) {
            log.warn("Rate limit exceeded for user {} on method {}", uuid, key);
            throw new AppException("Rate limit exceeded. Please try again later.");
        }
    }

    @PostConstruct
    public void init() {
        cleanerService = Executors.newSingleThreadScheduledExecutor();
        cleanerService.scheduleAtFixedRate(this::cleanUpOldEntries, 1, 1, TimeUnit.HOURS);
    }

    @PreDestroy
    public void destroy() {
        if (cleanerService != null && !cleanerService.isShutdown()) {
            cleanerService.shutdown();
        }
    }

    private void cleanUpOldEntries() {
        long expirationTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1);
        timestamps.entrySet().removeIf(entry -> {
            if (entry.getValue() < expirationTime) {
                limiters.remove(entry.getKey());
                return true;
            }
            return false;
        });
        log.info("Cleaned up old rate limiter entries");
    }
}

package com.memes.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.memes.annotation.AuthRequired;
import com.memes.exception.AppException;
import com.memes.util.Preconditions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class Auth {

    private final String localToken;
    private final String activeProfile;

    public Auth(@Value("${token:#{T(java.util.UUID).randomUUID().toString()}}") String localToken,
        @Value("${spring.profiles.active}") String activeProfile) {
        this.localToken = localToken;
        this.activeProfile = activeProfile;
        log.info("Auth aspect initialized. Local token is masked. Active profile: {}", activeProfile);
    }

    @Pointcut("@annotation(authenticationRequired)")
    public void authenticationPointcut(AuthRequired authenticationRequired) {
    }

    @Around(value = "authenticationPointcut(authenticationRequired)", argNames = "joinPoint,authenticationRequired")
    public Object authenticate(ProceedingJoinPoint joinPoint, AuthRequired authenticationRequired) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Preconditions.checkNotNull(attributes, AppException.unauthorized("Request context is not available."));
        HttpServletRequest request = attributes.getRequest();
        String httpMethod = request.getMethod();
        String path = String.format("%s %s", httpMethod, request.getRequestURI());
        String token = request.getHeader("token");
        String clientIp = request.getRemoteAddr();

        if ("dev".equals(activeProfile)) {
            log.debug("Skipping authentication in 'dev' profile for path: {}", path);
            return joinPoint.proceed();
        }

        if (token == null || !token.equals(localToken)) {
            log.warn("Unauthorized access attempt from IP: {} to path: {}", clientIp, path);
            log.trace("Unauthorized access attempt token: {}", token);
            throw AppException.unauthorized(String.format("Invalid token for: %s ", path));
        }

        log.info("Authorized access to path: {}", path);
        return joinPoint.proceed();
    }
}

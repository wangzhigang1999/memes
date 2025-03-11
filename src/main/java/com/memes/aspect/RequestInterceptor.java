package com.memes.aspect;

import com.memes.config.AppConfig;
import com.memes.exception.AppException;
import com.memes.util.Preconditions;
import com.memes.util.ResourceChecker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@AllArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {

    AppConfig config;

    String localToken;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)
        throws Exception {
        String url = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader("token");
        if (url.startsWith("/admin") || "OPTIONS".equals(method) || localToken.equals(token)
            || ResourceChecker.isStaticResource(url)) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        if (config.serverDown) {
            log.warn("Server is down manually,rejecting request...");
            throw AppException.serverDown();
        }
        String uuid = request.getHeader("uuid");
        Preconditions.checkStringNotEmpty(uuid, AppException.invalidParam("uuid"));
        if (config.uidBlacklist.contains(uuid)) {
            log.warn("Blacklisted user: {}", uuid);
            throw AppException.forbidden();
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
        @NotNull Object handler, Exception ex)
        throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}

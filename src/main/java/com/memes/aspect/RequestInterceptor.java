package com.memes.aspect;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import com.memes.config.AppConfig;
import com.memes.exception.AppException;
import com.memes.util.Preconditions;
import com.memes.util.ResourceChecker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "token";
    private static final String UUID_HEADER = "uuid";
    private static final String OPTIONS_METHOD = "OPTIONS";

    private final AppConfig config;
    private final String localToken;
    private final List<String> adminPathPrefixes;

    private final String activeProfile;

    public RequestInterceptor(AppConfig config, String localToken, List<String> adminPathPrefixes, String activeProfile) {
        this.config = config;
        this.localToken = localToken;
        this.adminPathPrefixes = adminPathPrefixes;
        this.activeProfile = activeProfile;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        String url = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader(TOKEN_HEADER);
        String uuid = request.getHeader(UUID_HEADER);

        // 1. Bypass certain requests
        if (shouldBypassAuthentication(url, method, token)) {
            return true;
        }

        // 2. Check server status
        checkServerStatus();

        // 3. Validate UUID
        validateUuid(uuid);
        return true;
    }

    private boolean shouldBypassAuthentication(String url, String method, String token) {
        return isAdminPath(url)
            || OPTIONS_METHOD.equals(method)
            || localToken.equals(token)
            || ResourceChecker.isStaticResource(url)
            || activeProfile.equals("dev");
    }

    private boolean isAdminPath(String url) {
        return adminPathPrefixes.stream().anyMatch(url::startsWith);
    }

    private void checkServerStatus() {
        if (config.isServerDown()) {
            log.warn("Server is down manually, rejecting request...");
            throw AppException.serverDown();
        }
    }

    private void validateUuid(String uuid) {
        Preconditions.checkStringNotEmpty(uuid, AppException.invalidParam("uuid"));
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
        @NotNull Object handler, Exception ex) throws Exception {
        // 可以添加一些请求完成后的清理工作
    }
}

package com.memes.config;

import com.memes.aspect.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.UUID;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    final AppConfig config;

    @Value("${token}")
    String adminToken = UUID.randomUUID().toString();

    public WebMvcConfig(AppConfig config) {
        this.config = config;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestInterceptor(config, adminToken, List.of("/api/admin"))).addPathPatterns("/**");
    }
}

package com.bupt.memes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;

@Configuration
public class ConversionConfig {

    @Bean
    public DefaultFormattingConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }
}

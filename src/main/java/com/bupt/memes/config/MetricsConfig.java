package com.bupt.memes.config;

import com.bupt.memes.aspect.Audit;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

@Configuration
public class MetricsConfig {

    @Value("${spring.application.name}")
    String applicationName;

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> addCommonTags() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown";
        }
        List<Tag> tags = new LinkedList<>();
        tags.add(Tag.of("hostname", hostname));
        tags.add(Tag.of("applicationName", applicationName));
        tags.add(Tag.of("instance", Audit.instanceUUID));
        return registry -> registry.config().commonTags(tags);
    }
}
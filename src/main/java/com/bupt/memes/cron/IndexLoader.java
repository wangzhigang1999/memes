package com.bupt.memes.cron;

import com.bupt.memes.config.AppConfig;
import com.bupt.memes.service.AnnIndexService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
public class IndexLoader {

    private final AnnIndexService annIndexService;
    private final AppConfig appConfig;

    public IndexLoader(AnnIndexService annIndexService, AppConfig appConfig) {
        this.annIndexService = annIndexService;
        this.appConfig = appConfig;
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void reload() {
        annIndexService.reloadIndex(appConfig.indexVersion, appConfig.indexFile, false);
        annIndexService.initKafkaConsumer();
    }
}

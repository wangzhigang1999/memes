package com.bupt.memes.cron;

import com.bupt.memes.config.AppConfig;
import com.bupt.memes.service.AnnIndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("indexerCron")
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
@Slf4j
public class Indexer {

    private final AnnIndexService annIndexService;
    private final AppConfig appConfig;

    public Indexer(AnnIndexService annIndexService, AppConfig appConfig) {
        this.annIndexService = annIndexService;
        this.appConfig = appConfig;
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void reload() {
        annIndexService.reloadIndex(appConfig.indexVersion, appConfig.indexFile, false);
        annIndexService.initKafkaConsumer();
    }

    @Scheduled(cron = "0 30 * * * ?")
    public void persist() {
        var pair = annIndexService.persistIndex();
        if (pair != null) {
            appConfig.setIndexFile(pair.getSecond());
            appConfig.setIndexVersion(pair.getFirst());
            log.info("Persist index to {}", pair.getSecond());
        }
    }
}

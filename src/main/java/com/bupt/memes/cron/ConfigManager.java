package com.bupt.memes.cron;

import com.bupt.memes.config.ConfigUpdater;
import com.bupt.memes.model.ConfigItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component()
@Slf4j
public class ConfigManager {

    final MongoTemplate template;

    final ConfigUpdater updater;

    Map<String, String> config = new ConcurrentHashMap<>();

    public ConfigManager(MongoTemplate template, ConfigUpdater updater) {
        this.template = template;
        this.updater = updater;
    }

    @Scheduled(fixedRate = 5 * 1000)
    public void updateConfig() {
        List<ConfigItem> configItems = template.findAll(ConfigItem.class);
        for (ConfigItem configItem : configItems) {
            if (config.containsKey(configItem.getKey()) && config.get(configItem.getKey()).equals(configItem.getValue())) {
                continue;
            }

            if (configItem.getKey() == null || configItem.getValue() == null) {
                log.warn("Config update failed, exist null value: {}={}", configItem.getKey(), configItem.getValue());
                continue;
            }

            boolean notified = updater.notifyUpdate(configItem.getKey(), configItem.getValue());
            if (notified) {
                this.config.put(configItem.getKey(), configItem.getValue());
                log.info("Config updated: {}={}", configItem.getKey(), configItem.getValue());
            } else {
                log.warn("Config update failed: {}={}", configItem.getKey(), configItem.getValue());
            }
        }
    }

}

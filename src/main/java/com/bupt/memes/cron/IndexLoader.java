package com.bupt.memes.cron;

import com.bupt.memes.model.Sys;
import com.bupt.memes.service.AnnIndexService;
import com.bupt.memes.service.SysConfigService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IndexLoader {

    private final AnnIndexService annIndexService;
    private final SysConfigService sysConfig;

    public IndexLoader(AnnIndexService annIndexService, SysConfigService sysConfig) {
        this.annIndexService = annIndexService;
        this.sysConfig = sysConfig;
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void reload() {
        Sys sys = sysConfig.getSys();
        annIndexService.reloadIndex(sys.getIndexVersion(), sys.getIndexFile(), false);
        annIndexService.initKafkaConsumer();
    }
}

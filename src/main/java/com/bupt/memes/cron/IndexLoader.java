package com.bupt.memes.cron;

import com.bupt.memes.model.Sys;
import com.bupt.memes.service.AnnIndex;
import com.bupt.memes.service.SysConfigService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IndexLoader {

    private final AnnIndex annIndex;
    private final SysConfigService sysConfig;

    public IndexLoader(AnnIndex annIndex, SysConfigService sysConfig) {
        this.annIndex = annIndex;
        this.sysConfig = sysConfig;
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void reload() {
        Sys sys = sysConfig.getSys();
        annIndex.reloadIndex(sys.getIndexVersion(), sys.getIndexFile(), false);
    }
}

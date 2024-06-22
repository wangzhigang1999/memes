package com.bupt.memes.controller.manager;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.config.AppConfig;
import com.bupt.memes.model.ConfigItem;
import com.bupt.memes.service.StatisticService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class Common {

    final StatisticService statisticService;

    final AppConfig appConfig;

    /**
     * 验证 token
     * 通过 aop 实现，不需要传参
     */
    @RequestMapping("/verify")
    @AuthRequired
    public Boolean verify() {
        return true;
    }

    /**
     * 统计从 00:00:00 到现在的信息
     */
    @RequestMapping("/statistic")
    @AuthRequired
    public Map<String, Object> statistic() {
        return statisticService.statistic();
    }

    /**
     * 获取爬虫的状态
     * 这个接口单独提供，crawler 服务会定时调用这个接口
     */
    @GetMapping("/bot/status")
    public Boolean botStatus() {
        return appConfig.botUp;
    }

    @GetMapping("/config")
    @AuthRequired
    public List<ConfigItem> getAppConfig() {
        return appConfig.getSys();
    }

    @PostMapping("/config")
    @AuthRequired
    public Boolean setAppConfig(@RequestBody Map<String, String> config) {
        return appConfig.updateConfig(config);
    }

}

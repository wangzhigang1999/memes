package com.bupt.memes.controller.common;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.model.Sys;
import com.bupt.memes.service.StatisticService;
import com.bupt.memes.service.SysConfigService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class Common {

    final StatisticService statisticService;

    final SysConfigService configService;

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
     * 设置爬虫的状态
     *
     * @return 是否成功
     */
    @PostMapping("/bot/status/{status}")
    @AuthRequired
    public Boolean enableBot(@PathVariable String status) {
        return "enable".equals(status) ? configService.enableBot() : configService.disableBot();
    }

    /**
     * 获取爬虫的状态
     */
    @GetMapping("/bot/status")
    public Boolean botStatus() {
        return SysConfigService.getBotStatus();
    }

    @GetMapping("/sys")
    @AuthRequired
    public Sys getConfigService() {
        return configService.getSys();
    }

}

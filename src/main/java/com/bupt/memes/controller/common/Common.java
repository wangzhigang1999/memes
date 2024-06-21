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

    final SysConfigService systemService;

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
        return "enable".equals(status) ? systemService.enableBot() : systemService.disableBot();
    }

    /**
     * 获取爬虫的状态
     */
    @GetMapping("/bot/status")
    public Boolean botStatus() {
        return systemService.botUp();
    }

    @GetMapping("/sys")
    @AuthRequired
    public Sys getSystemService() {
        return systemService.getSys();
    }

    @PostMapping("/topK/{topK}")
    @AuthRequired
    public Boolean setTopK(@PathVariable int topK) {
        return systemService.setTopK(topK);
    }

    // cache size
    @PostMapping("/cacheSize/{cacheSize}")
    @AuthRequired
    public Boolean setCacheSize(@PathVariable int cacheSize) {
        return systemService.setCacheSize(cacheSize);
    }

    @GetMapping("/gc")
    @AuthRequired
    public Boolean gc() {
        System.gc();
        return true;
    }

}

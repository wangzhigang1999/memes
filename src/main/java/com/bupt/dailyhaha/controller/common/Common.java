package com.bupt.dailyhaha.controller.common;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.Sys;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.service.StatisticService;
import com.bupt.dailyhaha.service.SysConfigService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class Common {

    final StatisticService statisticService;

    final SysConfigService sysConfig;

    /**
     * 验证token
     */
    @RequestMapping("/verify")
    @AuthRequired
    public ResultData<Boolean> verify() {
        return ResultData.success(true);
    }


    /**
     * 统计从00:00:00到现在的信息
     */
    @RequestMapping("/statistic")
    @AuthRequired
    public ResultData<Map<String, Object>> statistic() {
        return ResultData.success(statisticService.statistic());
    }

    /**
     * 开启爬虫
     *
     * @return 是否成功
     */
    @RequestMapping("/bot/enable")
    @AuthRequired
    public ResultData<Boolean> stopBot() {
        return ResultData.success(sysConfig.enableBot());
    }

    /**
     * 关闭爬虫
     *
     * @return 是否成功
     */
    @RequestMapping("/bot/disable")
    @AuthRequired
    public ResultData<Boolean> startBot() {
        return ResultData.success(sysConfig.disableBot());
    }

    /**
     * 获取爬虫的状态
     */
    @RequestMapping("/bot/status")
    public ResultData<Boolean> botStatus() {
        return ResultData.success(sysConfig.botStatus());
    }


    /**
     * 设置投稿发布策略
     *
     * @param strategy 策略名称
     */
    @PostMapping("/release/strategy")
    @AuthRequired
    public ResultData<Boolean> setStrategy(@RequestParam("strategy") String strategy) {
        return ResultData.success(sysConfig.setReleaseStrategy(strategy));
    }


    /**
     * 设置允许用户查看的最大历史记录数量
     *
     * @param max 最大历史记录数量 7 < max < 30
     */
    @PostMapping("/history/max")
    @AuthRequired
    public ResultData<Boolean> setMaxHistory(@RequestParam("max") int max) {
        return ResultData.success(sysConfig.setMaxHistory(max));
    }

    @GetMapping("/sys")
    @AuthRequired
    public ResultData<Sys> getSysConfig() {
        return ResultData.success(sysConfig.getSys());
    }
}

package com.bupt.dailyhaha.controller.common;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.service.Statistic;
import com.bupt.dailyhaha.service.SysConfig;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class Common {

    final Statistic statistic;

    final SysConfig sysConfig;

    /**
     * 验证token
     */
    @RequestMapping("/verify")
    @AuthRequired
    public ResultData<Boolean> verify() {
        return ResultData.success(true);
    }


    /**
     * 统计从00:00:00到现在的状态
     */
    @RequestMapping("/statistic")
    @AuthRequired
    public ResultData<Map<String, Object>> statistic() {
        return ResultData.success(statistic.statistic());
    }

    @RequestMapping("/bot/enable")
    @AuthRequired
    public ResultData<Boolean> stopBot() {
        return ResultData.success(sysConfig.enableBot());
    }

    @RequestMapping("/bot/disable")
    @AuthRequired
    public ResultData<Boolean> startBot() {
        return ResultData.success(sysConfig.disableBot());
    }

    @RequestMapping("/bot/status")
    public ResultData<Boolean> botStatus() {
        return ResultData.success(sysConfig.botStatus());
    }

    @GetMapping("/release/strategy")
    public ResultData<Object> getStrategy() {
        Set<String> releaseStrategy = sysConfig.getReleaseStrategy();
        String selectedReleaseStrategy = sysConfig.getSelectedReleaseStrategy();
        Map<String, Object> map = Map.of("releaseStrategy", releaseStrategy, "selectedReleaseStrategy", selectedReleaseStrategy);
        return ResultData.success(map);
    }

    @PostMapping("/release/strategy")
    @AuthRequired
    public ResultData<Boolean> setStrategy(@RequestParam("strategy") String strategy) {
        return ResultData.success(sysConfig.setReleaseStrategy(strategy));
    }
}

package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.ReturnCode;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.ReviewService;
import com.bupt.dailyhaha.service.StatisticService;
import com.bupt.dailyhaha.service.SubmissionService;
import com.bupt.dailyhaha.service.SysConfig;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    final SubmissionService service;

    final StatisticService statistic;

    final ReviewService reviewService;

    final SysConfig sysConfig;


    public AdminController(SubmissionService service, StatisticService statistic, ReviewService reviewService, SysConfig sysConfig) {
        this.service = service;
        this.statistic = statistic;
        this.reviewService = reviewService;
        this.sysConfig = sysConfig;
    }


    /**
     * 发布今天的提交
     *
     * @return ResultData
     */
    @RequestMapping("/release")
    @AuthRequired
    public ResultData<Integer> release() {
        int release = reviewService.release();
        return release < 0 ? ResultData.fail(ReturnCode.RC500) : ResultData.success(release);
    }

    /**
     * 验证token
     */
    @RequestMapping("/verify")
    @AuthRequired
    public ResultData<Boolean> verify() {
        return ResultData.success(true);
    }

    /**
     * 置顶
     *
     * @param hash hash
     * @return ResultData
     */
    @PostMapping("/top/{hash}")
    @AuthRequired
    public ResultData<Boolean> top(@PathVariable("hash") int hash) {
        return ResultData.success(sysConfig.addTop(hash));
    }

    /**
     * 取消置顶
     *
     * @param hash hash
     * @return ResultData
     */
    @DeleteMapping("/top/{hash}")
    @AuthRequired
    public ResultData<Boolean> unTop(@PathVariable("hash") int hash) {
        return ResultData.success(sysConfig.removeTop(hash));
    }

    /**
     * 获取置顶
     */
    @GetMapping("/top")
    public ResultData<Set<Submission>> getTop() {
        return ResultData.success(sysConfig.getTop());
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

    @GetMapping("/max/submission")
    public ResultData<Integer> getMaxSubmissions() {
        return ResultData.success(sysConfig.getMaxSubmissions());
    }

    @PostMapping("/max/submission")
    @AuthRequired
    public ResultData<Boolean> setMaxSubmissions(@RequestParam("max") int max) {
        return ResultData.success(sysConfig.setMaxSubmissions(max));
    }

}

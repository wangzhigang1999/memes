package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.ReturnCode;
import com.bupt.dailyhaha.service.ReviewService;
import com.bupt.dailyhaha.service.StatisticService;
import com.bupt.dailyhaha.service.SubmissionService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    final SubmissionService service;

    final StatisticService statistic;

    final ReviewService reviewService;

    public AdminController(SubmissionService service, StatisticService statistic, ReviewService reviewService) {
        this.service = service;
        this.statistic = statistic;
        this.reviewService = reviewService;
    }


    /**
     * 发布今天的提交
     *
     * @return ResultData
     */
    @RequestMapping("/release")
    @AuthRequired
    public ResultData<Boolean> release() {
        return !reviewService.release() ? ResultData.fail(ReturnCode.RC500) : ResultData.success(true);
    }

    /**
     * 统计从00:00:00到现在的状态
     */
    @RequestMapping("/statistic")
    @AuthRequired
    public ResultData<Map<String, Object>> statistic() {
        return ResultData.success(statistic.statistic());
    }
}

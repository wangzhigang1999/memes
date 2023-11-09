package com.bupt.memes.controller.submission;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.service.Interface.Review;
import com.bupt.memes.service.StatisticService;
import com.bupt.memes.service.SysConfigService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/submission")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SubAdmin {

    final ISubmission service;

    final StatisticService statisticService;

    final Review review;


    final SysConfigService sysConfig;


    /**
     * 发布今天的提交
     *
     * @return ResultData
     */
    @RequestMapping("/release")
    @AuthRequired
    public Integer release() {
        return review.release();
    }

    /**
     * 置顶
     *
     * @param id id
     * @return ResultData
     */
    @PostMapping("/top/{id}")
    @AuthRequired
    public Boolean top(@PathVariable("id") String id) {
        return sysConfig.addTop(id);
    }

    /**
     * 取消置顶
     *
     * @param id id
     * @return ResultData
     */
    @DeleteMapping("/top/{id}")
    @AuthRequired
    public Boolean unTop(@PathVariable("id") String id) {
        return sysConfig.removeTop(id);
    }


    /**
     * 设置每天的最少投稿数目,
     * 当天的投稿数目小于这个数目时，会自动的开启bot；
     * 大于这个数目时，会关闭bot
     *
     * @param min 最少投稿数目
     * @return ResultData
     */
    @PostMapping("/min")
    @AuthRequired
    public Boolean setMaxSubmissions(@RequestParam("min") int min) {
        return sysConfig.setMinSubmissions(min);
    }

}

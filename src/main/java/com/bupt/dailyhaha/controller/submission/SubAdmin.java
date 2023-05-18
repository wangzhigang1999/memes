package com.bupt.dailyhaha.controller.submission;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.common.ReturnCode;
import com.bupt.dailyhaha.service.Interface.Review;
import com.bupt.dailyhaha.service.Interface.Submission;
import com.bupt.dailyhaha.service.Statistic;
import com.bupt.dailyhaha.service.SysConfigService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/submission")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SubAdmin {

    final Submission service;

    final Statistic statistic;

    final Review review;


    final SysConfigService sysConfig;


    /**
     * 发布今天的提交
     *
     * @return ResultData
     */
    @RequestMapping("/release")
    @AuthRequired
    public ResultData<Integer> release() {
        int release = review.release();
        return release < 0 ? ResultData.fail(ReturnCode.RC500) : ResultData.success(release);
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
     * 获取每天的最少投稿数目
     */
    @GetMapping("/min")
    @AuthRequired
    public ResultData<Integer> getMinSubmissions() {
        return ResultData.success(sysConfig.getMinSubmissions());
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
    public ResultData<Boolean> setMaxSubmissions(@RequestParam("min") int min) {
        return ResultData.success(sysConfig.setMinSubmissions(min));
    }

}

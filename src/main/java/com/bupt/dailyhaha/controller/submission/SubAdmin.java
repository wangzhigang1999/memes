package com.bupt.dailyhaha.controller.submission;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.common.ReturnCode;
import com.bupt.dailyhaha.service.*;
import com.bupt.dailyhaha.service.Interface.BBSTask;
import com.bupt.dailyhaha.service.Interface.Doc;
import com.bupt.dailyhaha.service.Interface.Review;
import com.bupt.dailyhaha.service.Interface.Submission;
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

    final Doc doc;

    final BBSTask bbsTask;

    final SysConfig sysConfig;


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


    @GetMapping("/max")
    public ResultData<Integer> getMaxSubmissions() {
        return ResultData.success(sysConfig.getMaxSubmissions());
    }

    @PostMapping("/max")
    @AuthRequired
    public ResultData<Boolean> setMaxSubmissions(@RequestParam("max") int max) {
        return ResultData.success(sysConfig.setMaxSubmissions(max));
    }

}

package com.bupt.memes.controller.review;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.model.common.ResultData;
import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.Review;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/review")
@CrossOrigin(origins = "*")
public class ReviewAdmin {
    final Review service;

    public ReviewAdmin(Review service) {
        this.service = service;
    }

    /**
     * 获取所有待审核的提交
     *
     * @return ResultData
     */
    @GetMapping("")
    @AuthRequired
    public ResultData<List<Submission>> all() {
        return ResultData.success(service.getWaitingSubmissions());
    }

    /**
     * 接受某个投稿
     *
     * @param id 投稿的id
     * @return ResultData
     */
    @PostMapping("/accept/{id}")
    @AuthRequired
    public ResultData<Boolean> accept(@PathVariable("id") String id) {
        return ResultData.success(service.acceptSubmission(id));
    }

    /**
     * 拒绝某个投稿
     *
     * @param id 投稿的 id
     * @return ResultData
     */
    @PostMapping("/reject/{id}")
    @AuthRequired
    public ResultData<Boolean> reject(@PathVariable("id") String id) {
        return ResultData.success(service.rejectSubmission(id));
    }

    /**
     * 批量接受投稿
     *
     * @param ids 投稿的 id
     * @return ResultData
     */
    @PostMapping("/accept/batch")
    @AuthRequired
    public ResultData<Integer> batchAccept(@RequestBody List<String> ids) {
        return ResultData.success(service.batchAcceptSubmission(ids));
    }

    /**
     * 批量拒绝投稿
     *
     * @param ids 投稿的 id
     * @return Integer
     */
    @PostMapping("/reject/batch")
    @AuthRequired
    public Integer batchReject(@RequestBody List<String> ids) {
        return service.batchRejectSubmission(ids);
    }


    /**
     * 今日投稿的统计信息
     */
    @GetMapping("/statistic")
    @AuthRequired
    public ResultData<Map<String, Integer>> today() {
        return ResultData.success(service.getTodayInfo());
    }
}

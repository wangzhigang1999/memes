package com.bupt.memes.controller.manager;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.model.media.Submission;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/review")
@CrossOrigin(origins = "*")
public class Review {
    final com.bupt.memes.service.Interface.Review service;

    public Review(com.bupt.memes.service.Interface.Review service) {
        this.service = service;
    }

    /**
     * 获取所有待审核的提交
     *
     * @return ResultData
     */
    @GetMapping("")
    @AuthRequired
    public List<Submission> all() {
        return service.getWaitingSubmissions();
    }

    /**
     * 接受某个投稿
     *
     * @param id
     *            投稿的 id
     * @return ResultData
     */
    @PostMapping("/accept/{id}")
    @AuthRequired
    public Boolean accept(@PathVariable("id") String id) {
        return service.acceptSubmission(id);
    }

    /**
     * 拒绝某个投稿
     *
     * @param id
     *            投稿的 id
     * @return ResultData
     */
    @PostMapping("/reject/{id}")
    @AuthRequired
    public Boolean reject(@PathVariable("id") String id) {
        return service.rejectSubmission(id);
    }

    /**
     * 批量接受投稿
     *
     * @param ids
     *            投稿的 id
     * @return ResultData
     */
    @PostMapping("/accept/batch")
    @AuthRequired
    public Integer batchAccept(@RequestBody List<String> ids) {
        return service.batchAcceptSubmission(ids);
    }

    /**
     * 批量拒绝投稿
     *
     * @param ids
     *            投稿的 id
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
    public Map<String, Long> today() {
        return service.getTodayInfo();
    }
}

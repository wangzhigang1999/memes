package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "*")
public class ReviewController {
    final ReviewService service;

    public ReviewController(ReviewService service) {
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
        return ResultData.success(service.listSubmissions());
    }

    /**
     * 接受某个投稿
     *
     * @param hashcode 投稿的hashcode
     * @return ResultData
     */
    @PostMapping("/accept/{hashcode}")
    @AuthRequired
    public ResultData<Boolean> accept(@PathVariable("hashcode") int hashcode) {
        return ResultData.success(service.acceptSubmission(hashcode));
    }

    /**
     * 拒绝某个投稿
     *
     * @param hashcode 投稿的hashcode
     * @return ResultData
     */
    @PostMapping("/reject/{hashcode}")
    @AuthRequired
    public ResultData<Boolean> reject(@PathVariable("hashcode") int hashcode) {
        return ResultData.success(service.rejectSubmission(hashcode));
    }

    /**
     * 批量接受投稿
     *
     * @param hashcode 投稿的hashcode
     * @return ResultData
     */
    @PostMapping("/accept/batch")
    @AuthRequired
    public ResultData<Integer> batchAccept(@RequestBody List<Integer> hashcode) {
        return ResultData.success(service.batchAcceptSubmission(hashcode));
    }
}

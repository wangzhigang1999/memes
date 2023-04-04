package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResultData<List<Submission>> all() {
        return ResultData.success(service.listSubmissions());
    }

    @PostMapping("/accept/{hashcode}")
    public ResultData<Boolean> accept(@PathVariable("hashcode") int hashcode) {
        return ResultData.success(service.acceptSubmission(hashcode));
    }

    @PostMapping("/reject/{hashcode}")
    public ResultData<Boolean> reject(@PathVariable("hashcode") int hashcode) {
        return ResultData.success(service.rejectSubmission(hashcode));
    }
}

package com.memes.controller.manager;

import com.memes.annotation.AuthRequired;
import com.memes.model.submission.Submission;
import com.memes.service.review.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/review")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ReviewManagerController {

    final ReviewService service;

    @GetMapping("")
    @AuthRequired
    public List<Submission> listWaitingSubmissions(Integer limit) {
        return service.listWaitingSubmission(limit);
    }

    @PostMapping("/{operation}/{id}")
    @AuthRequired
    public Boolean review(@PathVariable("operation") String operation, @PathVariable("id") String id) {
        return service.reviewSubmission(id, operation);
    }

    @PostMapping("/batch/{operation}")
    @AuthRequired
    public Integer batchReview(@RequestBody List<String> ids, @PathVariable("operation") String operation) {
        return service.batchReviewSubmission(ids, operation);
    }

    @GetMapping("/statistic/{status}")
    @AuthRequired
    public long getStatusNum(@PathVariable("status") String status) {
        return service.getStatusNum(status);
    }
}

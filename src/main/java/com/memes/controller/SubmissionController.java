package com.memes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.memes.annotation.AuthRequired;
import com.memes.model.pojo.Submission;
import com.memes.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/submission")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("/{id}")
    public Submission getById(@PathVariable Long id) {
        return submissionService.getById(id);
    }

    @GetMapping
    public List<Submission> list(@RequestParam(defaultValue = "20") Integer pageSize, Long lastId, String date) {
        return submissionService.list(pageSize, lastId, date);
    }

    @AuthRequired
    @PutMapping("/{id}")
    public Submission update(@PathVariable Long id, @RequestBody Submission submission) {
        submission.setId(id);
        submissionService.updateById(submission);
        return submission;
    }

    @AuthRequired
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        submissionService.removeById(id);
    }

    @PostMapping("/{id}/feedback/{isLike}")
    public Submission updateSubmissionFeedbackCount(@PathVariable Long id, @PathVariable boolean isLike) {
        return submissionService.updateSubmissionCount(id, isLike);
    }

    @AuthRequired
    @PostMapping("/{id}/pin")
    public boolean pinSubmission(@PathVariable Long id) {
        return submissionService.pinSubmission(id);
    }

    @AuthRequired
    @DeleteMapping("/{id}/pin")
    public boolean unpinSubmission(@PathVariable Long id) {
        return submissionService.unpinSubmission(id);
    }

    @GetMapping("/pinned")
    public List<Submission> listPinnedSubmission() {
        return submissionService.listPinnedSubmission();
    }
}

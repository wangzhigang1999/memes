package com.memes.controller;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memes.model.pojo.Submission;
import com.memes.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public Submission create(@RequestBody Submission submission) {
        submissionService.save(submission);
        return submission;
    }

    @GetMapping("/{id}")
    public Submission getById(@PathVariable Integer id) {
        return submissionService.getById(id);
    }

    @GetMapping
    public Page<Submission> list(@RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size) {
        return submissionService.page(new Page<>(page, size));
    }

    @PutMapping("/{id}")
    public Submission update(@PathVariable Integer id, @RequestBody Submission submission) {
        submission.setId(id);
        submissionService.updateById(submission);
        return submission;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        submissionService.removeById(id);
    }
}

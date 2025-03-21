package com.memes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.memes.annotation.AuthRequired;
import com.memes.model.pojo.Submission;
import com.memes.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("/{id}")
    public Submission getById(@PathVariable Integer id) {
        return submissionService.getById(id);
    }

    @GetMapping
    public List<Submission> list(@RequestParam(defaultValue = "20") Integer pageSize, Long lastId, String date) {
        return submissionService.list(pageSize, lastId, date);
    }

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
    public Submission updateSubmissionCount(@PathVariable Long id, @PathVariable boolean isLike) {
        return submissionService.updateSubmissionCount(id, isLike);
    }
}

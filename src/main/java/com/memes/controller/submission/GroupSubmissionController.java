package com.memes.controller.submission;

import com.memes.annotation.AuthRequired;
import com.memes.model.submission.SubmissionGroup;
import com.memes.service.submission.SubGroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/submission/group")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class GroupSubmissionController {

    final SubGroupService subGroupService;

    // create
    @PutMapping("")
    @AuthRequired
    public SubmissionGroup create(@RequestBody List<String> submissionIds) {
        return subGroupService.createGroup(submissionIds);
    }

    // post
    @PostMapping("/{id}")
    @AuthRequired
    public SubmissionGroup update(@PathVariable("id") String id, @RequestBody List<String> submissionIds) {
        return subGroupService.addToGroup(id, submissionIds);
    }

    // get
    @GetMapping("/{id}")
    @AuthRequired
    public SubmissionGroup get(@PathVariable("id") String id) {
        return subGroupService.getById(id);
    }
}

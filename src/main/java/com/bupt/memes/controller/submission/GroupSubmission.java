package com.bupt.memes.controller.submission;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.exception.AppException;
import com.bupt.memes.model.media.SubmissionGroup;
import com.bupt.memes.service.Interface.ISubGroup;
import com.bupt.memes.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/submission/group")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class GroupSubmission {

    final ISubGroup subGroup;

    // create
    @PutMapping("")
    @AuthRequired
    public Object create(@RequestBody List<String> submissionIds) {
        return subGroup.createGroup(submissionIds);
    }

    // post
    @PostMapping("/{id}")
    @AuthRequired
    public Object update(@PathVariable("id") String id, @RequestBody List<String> submissionIds) {
        SubmissionGroup submissionGroup = subGroup.addToGroup(id, submissionIds);
        Preconditions.checkArgument(submissionGroup != null, AppException.internalError("update submission group failed"));
        return submissionGroup;
    }

    // get
    @GetMapping("/{id}")
    @AuthRequired
    public Object get(@PathVariable("id") String id) {
        return subGroup.getById(id);
    }
}

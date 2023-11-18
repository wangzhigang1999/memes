package com.bupt.memes.controller.submission;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.service.Interface.IImageGroup;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/submission/imageGroup")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ImageGroup {

    final IImageGroup imageGroup;

    // create
    @PutMapping("")
    @AuthRequired
    public Object create(@RequestBody List<String> submissionIds) {
        return imageGroup.createImageGroup(submissionIds);
    }

    // post
    @PostMapping("/{id}")
    @AuthRequired
    public Object update(@PathVariable("id") String id, @RequestBody List<String> submissionIds) {
        return imageGroup.addToImageGroup(id, submissionIds);
    }
}

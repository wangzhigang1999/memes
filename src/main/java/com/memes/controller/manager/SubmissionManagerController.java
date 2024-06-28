package com.memes.controller.manager;

import com.memes.annotation.AuthRequired;
import com.memes.config.AppConfig;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/submission")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SubmissionManagerController {

    final AppConfig appConfig;

    @PostMapping("/top/{id}")
    @AuthRequired
    public Boolean addTop(@PathVariable("id") String id) {
        return appConfig.addTop(id);
    }

    @DeleteMapping("/top/{id}")
    @AuthRequired
    public Boolean removeTop(@PathVariable("id") String id) {
        return appConfig.removeTop(id);
    }

}

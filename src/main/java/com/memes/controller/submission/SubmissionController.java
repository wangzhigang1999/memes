package com.memes.controller.submission;

import com.memes.annotation.AuthRequired;
import com.memes.config.AppConfig;
import com.memes.exception.AppException;
import com.memes.model.common.PageResult;
import com.memes.model.param.ListSubmissionsRequest;
import com.memes.model.submission.Submission;
import com.memes.service.submission.SubmissionService;
import com.memes.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@RestController("submissionController")
@RequestMapping("/submission")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SubmissionController {

    final SubmissionService service;

    final AppConfig appConfig;

    /**
     * upload file
     *
     * @param file
     *            file，非必须
     * @param text
     *            text，可能是一个 url，将来会被嵌入到 iframe 中
     * @param mime
     *            mime,type
     * @return ResultData
     * @throws IOException
     *             IOException
     */
    @PostMapping("")
    public Submission upload(MultipartFile file, String text, String mime) throws IOException {
        Preconditions.checkArgument(mime != null && !mime.isEmpty(), AppException.invalidParam("mime"));
        Preconditions.checkArgument(text != null || file != null, AppException.invalidParam("file or text"));
        if (mime.startsWith("text")) {
            return service.storeTextFormatSubmission(text, mime);
        }
        Preconditions.checkArgument(file != null, AppException.invalidParam("file"));
        InputStream inputStream = file.getInputStream();
        var store = service.storeStreamSubmission(inputStream, mime);
        inputStream.close();
        return store;
    }

    @GetMapping("")
    public PageResult<Submission> listSubmissions(ListSubmissionsRequest request) {
        return service.listSubmissions(request);
    }

    /**
     * 获取置顶
     */
    @GetMapping("/top")
    public Set<Submission> getTop() {
        return appConfig.topSubmissions;
    }

    @PostMapping("/feedback/{id}/{feedback}")
    public Boolean feedback(@PathVariable("id") String id, @PathVariable("feedback") String feedback) {
        return service.feedback(id, feedback);
    }

    @GetMapping("/id/{id}")
    public Submission getSubmissionById(@PathVariable("id") String id) {
        return service.getSubmissionById(id);
    }

    @DeleteMapping("/id/{id}")
    @AuthRequired
    public Boolean deleteSubmissionById(@PathVariable("id") String id) {
        return service.markDelete(id);
    }

    @GetMapping("/similar/{id}")
    public List<Submission> getSimilarSubmission(@PathVariable("id") String id, Integer size) {
        return service.getSimilarSubmission(id, size);
    }
}

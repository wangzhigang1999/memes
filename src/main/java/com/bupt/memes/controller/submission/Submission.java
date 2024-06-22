package com.bupt.memes.controller.submission;

import com.bupt.memes.config.AppConfig;
import com.bupt.memes.exception.AppException;
import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.util.Preconditions;
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
public class Submission {

    final ISubmission service;

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
    public com.bupt.memes.model.media.Submission upload(MultipartFile file, String text, String mime) throws IOException {
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

    /**
     * 获取置顶
     */
    @GetMapping("/top")
    public Set<com.bupt.memes.model.media.Submission> getTop() {
        return appConfig.topSubmissions;
    }

    // 点赞
    @PostMapping("{id}/like")
    public Boolean like(@PathVariable("id") String id) {
        return service.vote(id, true);
    }

    // 点踩
    @PostMapping("{id}/dislike")
    public Boolean dislike(@PathVariable("id") String id) {
        return service.vote(id, false);
    }

    /**
     * 分页获取投稿
     *
     * @param pageSize
     *            页大小
     * @param lastID
     *            上一页最后一个的 id
     * @return Page
     */
    @GetMapping("/page")
    public PageResult<com.bupt.memes.model.media.Submission> getSubmissionByPage(int pageSize, String lastID) {
        return service.getSubmissionByPage(pageSize, lastID);
    }

    /**
     * 获取某个 id 的投稿
     *
     * @param id
     *            id
     * @return Submission
     */
    @GetMapping("/id/{id}")
    public com.bupt.memes.model.media.Submission getSubmissionById(@PathVariable("id") String id) {
        return service.getSubmissionById(id);
    }

    @DeleteMapping("/id/{id}")
    public Boolean deleteSubmissionById(@PathVariable("id") String id) {
        return service.markDelete(id);
    }

    /**
     * 获取某一天的提交
     *
     * @param date
     *            日期 YYYY-MM-DD
     * @return List
     */
    @GetMapping("/date/{date}")
    public List<com.bupt.memes.model.media.Submission> getSubmissionByDate(@PathVariable("date") String date) {
        return service.getSubmissionByDate(date);
    }

    @GetMapping("/similar/{id}")
    public List<com.bupt.memes.model.media.Submission> getSimilarSubmission(@PathVariable("id") String id, Integer size) {
        size = size == null ? 10 : Math.min(size, 50);
        return service.getSimilarSubmission(id, size);
    }

    @GetMapping("/random")
    public List<com.bupt.memes.model.media.Submission> randomSubmission(Integer size) {
        size = size == null ? 10 : Math.min(size, 50);
        return service.randomSubmission(size);
    }

}

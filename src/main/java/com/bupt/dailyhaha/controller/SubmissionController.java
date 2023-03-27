package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.ReturnCode;
import com.bupt.dailyhaha.pojo.submission.Submission;
import com.bupt.dailyhaha.service.Storage;
import com.bupt.dailyhaha.service.SubmissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/submission")
@CrossOrigin(origins = "*")
public class SubmissionController {

    final Storage storage;

    final SubmissionService service;


    public SubmissionController(Storage storage, SubmissionService service) {
        this.storage = storage;
        this.service = service;
    }

    @PostMapping("")
    public ResultData<Submission> upload(MultipartFile file, String uri, String mime, boolean personal) throws IOException {
        if (mime == null || mime.isEmpty()) {
            return ResultData.fail(ReturnCode.RC400);
        }
        // file is null and uri is null,bad request
        if (file == null && uri == null) {
            return ResultData.fail(ReturnCode.RC400);
        }
        if (mime.startsWith("text")) {
            return ResultData.success(service.storeTextFormatSubmission(uri, mime));
        }
        if (file == null) {
            return ResultData.fail(ReturnCode.RC400);
        }
        InputStream inputStream = file.getInputStream();
        Submission store = storage.store(inputStream, mime, personal);
        return store == null ? ResultData.fail(ReturnCode.RC500) : ResultData.success(store);
    }

    @PostMapping("/vote/{name}/{up}")
    public ResultData<Boolean> vote(@PathVariable("name") String name, @PathVariable("up") boolean up) {
        return ResultData.success(service.vote(name, up));
    }


    @GetMapping("/{date}")
    public ResultData<List<Submission>> getSubmission(@PathVariable("date") String date) {
        return ResultData.success(service.getHistory(date));
    }

    @GetMapping("/review")
    @AuthRequired
    public ResultData<List<Submission>> review() {
        return ResultData.success(service.getTodaySubmissions());
    }

    @DeleteMapping("/{name}")
    @AuthRequired
    public ResultData<Boolean> delete(@PathVariable("name") String name) {
        return ResultData.success(service.deleteByName(name));
    }

    @RequestMapping("/release")
    @AuthRequired
    public Object release() {
        List<Submission> today = service.getTodaySubmissions();
        boolean history = service.updateHistory(Utils.getYMD(), today);

        return !history ? ResultData.fail(ReturnCode.RC500) : ResultData.success(true);

    }
}
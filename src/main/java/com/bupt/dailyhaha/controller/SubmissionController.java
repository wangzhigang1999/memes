package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.ReturnCode;
import com.bupt.dailyhaha.pojo.Submission;
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

    /**
     * upload file
     *
     * @param file     file,非必须
     * @param uri      uri,可能是一个url,将来会被嵌入到iframe中
     * @param mime     mime,type
     * @param personal personal or not
     * @return ResultData
     * @throws IOException IOException
     */
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

    @PostMapping("/vote/{hash}/{up}")
    public ResultData<Boolean> vote(@PathVariable("hash") int hash, @PathVariable("up") boolean up) {
        return ResultData.success(service.vote(hash, up));
    }


    /**
     * 获取所有的历史日期
     *
     * @return ResultData
     */
    @GetMapping("/history")
    public ResultData<List<String>> getHistory() {
        return ResultData.success(service.getHistoryDates(7));
    }

    /**
     * 获取某一天的提交
     *
     * @param date 日期 YYYY-MM-DD
     * @return ResultData
     */
    @GetMapping("/{date}")
    public ResultData<List<Submission>> getSubmission(@PathVariable("date") String date) {
        return ResultData.success(service.getHistory(date));
    }

    /**
     * 实时获取今天的提交
     *
     * @return ResultData
     */
    @GetMapping("/review")
    @AuthRequired
    public ResultData<List<Submission>> review() {
        return ResultData.success(service.getTodaySubmissions());
    }

    /**
     * 删除某一个提交
     *
     * @param hash 可以认为是唯一的一个表示符
     * @return ResultData
     */
    @DeleteMapping("/{hash}")
    @AuthRequired
    public ResultData<Boolean> delete(@PathVariable("hash") int hash) {
        return ResultData.success(service.deleteByHashcode(hash));
    }

    /**
     * 发布今天的提交
     *
     * @return ResultData
     */
    @RequestMapping("/release")
    @AuthRequired
    public ResultData<Boolean> release() {
        List<Submission> today = service.getTodaySubmissions();
        boolean history = service.updateHistory(Utils.getYMD(), today);
        return !history ? ResultData.fail(ReturnCode.RC500) : ResultData.success(true);
    }
}
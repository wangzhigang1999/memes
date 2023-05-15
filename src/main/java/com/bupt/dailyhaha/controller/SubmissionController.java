package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.common.ReturnCode;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.SubmissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/submission")
@CrossOrigin(origins = "*")
public class SubmissionController {


    final SubmissionService service;


    public SubmissionController(SubmissionService service) {
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
        Submission store = service.storeStreamSubmission(inputStream, mime, personal);
        return store == null ? ResultData.fail(ReturnCode.RC500) : ResultData.success(store);
    }

    /**
     * 点赞或者踩
     *
     * @param hash hash
     * @param up   up or down
     * @return ResultData
     */

    @PostMapping("/vote/{hash}/{up}")
    public ResultData<Boolean> vote(@PathVariable("hash") int hash, @PathVariable("up") boolean up) {
        return ResultData.success(service.vote(hash, up));
    }

    @GetMapping("/page")
    public ResultData<PageResult<Submission>> getSubmissionByPage(int pageNum, int pageSize, String lastID) {
        return ResultData.success(service.getSubmissionByPage(pageNum, pageSize, lastID));
    }


}
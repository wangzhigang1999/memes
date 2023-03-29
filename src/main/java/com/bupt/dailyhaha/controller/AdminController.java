package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.ReturnCode;
import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.service.Storage;
import com.bupt.dailyhaha.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submission")
@CrossOrigin(origins = "*")
public class AdminController {
    final Storage storage;

    final SubmissionService service;

    public AdminController(Storage storage, SubmissionService service) {
        this.storage = storage;
        this.service = service;
    }


    /**
     * 实时获取今天的所有提交
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

package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.pojo.BBSRecord;
import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.service.BBSTask;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bbsTasks")
@CrossOrigin(origins = "*")
public class BBSController {

    final BBSTask bbsTask;

    public BBSController(BBSTask bbsTask) {
        this.bbsTask = bbsTask;
    }

    @GetMapping("")
    public ResultData<Object> getTasks(BBSRecord.Status status) {
        return ResultData.success(bbsTask.getTasks(status));
    }

    @PostMapping("/create")
    public ResultData<Boolean> create(@RequestBody BBSRecord.Post post) {
        return ResultData.success(bbsTask.create(post));
    }


    @PostMapping("/finish")
    public ResultData<Boolean> update(String id, BBSRecord.Status status) {
        return ResultData.success(bbsTask.setStatus(id, status));
    }
}

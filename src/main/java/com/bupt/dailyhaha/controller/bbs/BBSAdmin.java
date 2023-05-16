package com.bupt.dailyhaha.controller.bbs;

import com.bupt.dailyhaha.anno.AuthRequired;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.doc.BBSRecord;
import com.bupt.dailyhaha.service.Interface.BBSTask;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员接口
 * 包含着所有的管理员独有的接口
 */
@RestController
@RequestMapping("/admin/bbsTasks")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class BBSAdmin {

    final BBSTask bbsTask;

    @AuthRequired
    @GetMapping("")
    public ResultData<Object> getBBSTasks(BBSRecord.Status status) {
        return ResultData.success(bbsTask.getTasks(status));
    }

    @AuthRequired
    @PostMapping("/create")
    public ResultData<Boolean> createBBSTasks(@RequestBody BBSRecord.Post post) {
        return ResultData.success(bbsTask.create(post));
    }

    @AuthRequired
    @PostMapping("/finish")
    public ResultData<Boolean> markBBSTaskStatus(String id, BBSRecord.Status status) {
        return ResultData.success(bbsTask.setStatus(id, status));
    }
}

package com.bupt.dailyhaha.controller;

import com.bupt.dailyhaha.pojo.ResultData;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.HistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submission")
@CrossOrigin(origins = "*")
public class HistoryController {

    final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * 获取所有的历史日期
     *
     * @return ResultData
     */
    @GetMapping("/history")
    public ResultData<List<String>> getHistory() {
        return ResultData.success(historyService.getHistoryDates(7));
    }

    /**
     * 获取某一天的提交
     *
     * @param date 日期 YYYY-MM-DD
     * @return ResultData
     */
    @GetMapping("/{date}")
    public ResultData<List<Submission>> getSubmission(@PathVariable("date") String date) {
        return ResultData.success(historyService.getHistory(date));
    }

}

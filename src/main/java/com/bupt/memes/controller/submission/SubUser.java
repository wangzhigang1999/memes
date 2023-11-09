package com.bupt.memes.controller.submission;

import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.model.common.ResultData;
import com.bupt.memes.model.common.ReturnCode;
import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.IHistory;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.service.SysConfigService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/submission")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SubUser {

    final ISubmission service;

    final IHistory history;

    final SysConfigService sysConfig;


    /**
     * upload file
     *
     * @param file file,非必须
     * @param text text,可能是一个url,将来会被嵌入到iframe中
     * @param mime mime,type
     * @return ResultData
     * @throws IOException IOException
     */
    @PostMapping("")
    public ResultData<Submission> upload(MultipartFile file, String text, String mime) throws IOException {
        if (mime == null || mime.isEmpty()) {
            return ResultData.fail(ReturnCode.RC400);
        }
        // file is null and uri is null,bad request
        if (file == null && text == null) {
            return ResultData.fail(ReturnCode.RC400);
        }
        if (mime.startsWith("text")) {
            return ResultData.success(service.storeTextFormatSubmission(text, mime));
        }
        if (file == null) {
            return ResultData.fail(ReturnCode.RC400);
        }
        InputStream inputStream = file.getInputStream();
        var store = service.storeStreamSubmission(inputStream, mime, false);
        return store == null ? ResultData.fail(ReturnCode.RC500) : ResultData.success(store);
    }

    /**
     * 获取置顶
     */
    @GetMapping("/top")
    public Set<Submission> getTop() {
        return SysConfigService.sys.getTopSubmission();
    }

    /**
     * 点赞或者踩
     *
     * @param id id
     * @param up up or down
     * @return ResultData
     */
    @PostMapping("/vote/{id}/{up}")
    public Boolean vote(@PathVariable("id") String id, @PathVariable("up") boolean up) {
        return service.vote(id, up);
    }

    /**
     * 分页获取投稿
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param lastID   上一页最后一个的id
     * @return Page
     */
    @GetMapping("/page")
    public PageResult<Submission> getSubmissionByPage(int pageNum, int pageSize, String lastID) {
        return service.getSubmissionByPage(pageNum, pageSize, lastID);
    }

    /**
     * 获取所有的历史日期
     *
     * @return ResultData
     */
    @GetMapping("/history")
    public List<String> getHistory() {
        return history.getHistoryDates(sysConfig.getSys().getMAX_HISTORY());
    }

    /**
     * 获取某一天的提交
     *
     * @param date 日期 YYYY-MM-DD
     * @return ResultData
     */
    @GetMapping("/{date}")
    public List<Submission> getSubmission(@PathVariable("date") String date) {
        return history.getHistory(date);
    }

}

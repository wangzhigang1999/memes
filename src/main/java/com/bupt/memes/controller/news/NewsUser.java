package com.bupt.memes.controller.news;

import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.model.common.ResultData;
import com.bupt.memes.model.media.News;
import com.bupt.memes.service.Interface.INews;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class NewsUser {
    final INews iNews;

    @GetMapping("/id/{id}")
    public ResultData<News> findById(@PathVariable String id) {
        return ResultData.success(iNews.findById(id));
    }

    @GetMapping("/date/{date}")
    public ResultData<List<News>> findByDate(@PathVariable String date) {
        return ResultData.success(iNews.findByDate(date));
    }

    @GetMapping("/month/{month}/day/{day}")
    public ResultData<List<News>> findByMMDD(@PathVariable String month, @PathVariable String day) {
        return ResultData.success(iNews.findByMMDD(month, day));
    }


    @GetMapping("/page")
    public PageResult<News> page( Integer pageSize, String lastID) {

        if (pageSize == null) {
            pageSize = 10;
        }

        return iNews.find( false, pageSize, lastID);
    }
}

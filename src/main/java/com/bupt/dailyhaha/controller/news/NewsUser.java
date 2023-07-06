package com.bupt.dailyhaha.controller.news;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.common.ResultData;
import com.bupt.dailyhaha.pojo.media.News;
import com.bupt.dailyhaha.service.Interface.INews;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.bupt.dailyhaha.util.Utils.convertTag;

@RestController
@RequestMapping("/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class NewsUser {
    final INews iNews;

    @GetMapping("")
    public ResultData<News> findById(String id) {
        return ResultData.success(iNews.findById(id));
    }

    @GetMapping("/page")
    public ResultData<PageResult<News>> page(Integer pageNum, Integer pageSize, String lastID, @RequestParam(required = false) String tag) {
        // set default value for pageNum and pageSize
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        if (tag != null) {
            return ResultData.success(iNews.findByTag(convertTag(tag), pageNum, pageSize, lastID));
        }
        return ResultData.success(iNews.find(pageNum, pageSize, lastID));
    }
}

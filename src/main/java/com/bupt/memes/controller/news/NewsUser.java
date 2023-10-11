package com.bupt.memes.controller.news;

import com.bupt.memes.pojo.common.PageResult;
import com.bupt.memes.pojo.common.ResultData;
import com.bupt.memes.pojo.media.News;
import com.bupt.memes.service.Interface.INews;
import com.bupt.memes.util.Utils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bupt.memes.util.Utils.convertTag;

@RestController
@RequestMapping("/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class NewsUser {
    final INews iNews;

    @GetMapping("/{id}")
    public ResultData<News> findById(@PathVariable String id, HttpServletResponse response) {
        // cache forever
        response.setHeader("Cache-Control", "max-age=31536000");
        return ResultData.success(iNews.findById(id));
    }

    @GetMapping("/date/{date}")
    public ResultData<List<News>> findByDate(@PathVariable String date, HttpServletResponse response) {
        String ymd = Utils.getYMD();
        if (date.equals(ymd)) {
            // set cache for 1 hour
            response.setHeader("Cache-Control", "max-age=3600");
        } else {
            // set cache forever
            response.setHeader("Cache-Control", "max-age=31536000");
        }
        return ResultData.success(iNews.findByDate(date));
    }

    @GetMapping("/mm-dd/{mmdd}")
    public ResultData<List<News>> findByMMDD(@PathVariable String mmdd, HttpServletResponse response) {
        // set cache for 1 day
        response.setHeader("Cache-Control", "max-age=86400");
        return ResultData.success(iNews.findByMMDD(mmdd));
    }


    @GetMapping("/page")
    public ResultData<PageResult<News>> page(Integer pageNum, Integer pageSize, String lastID, @RequestParam(required = false) Boolean hasContent, @RequestParam(required = false) String tag) {
        // set default value for pageNum and pageSize
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        if (hasContent == null) {
            hasContent = false;
        }

        if (tag != null) {
            return ResultData.success(iNews.findByTag(convertTag(tag), hasContent, pageNum, pageSize, lastID));
        }
        return ResultData.success(iNews.find(pageNum, hasContent, pageSize, lastID));
    }
}

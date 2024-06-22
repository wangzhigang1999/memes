package com.bupt.memes.controller.news;

import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.service.Interface.INews;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("newsController")
@RequestMapping("/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class News {
    final INews iNews;

    @GetMapping("/id/{id}")
    public com.bupt.memes.model.media.News findById(@PathVariable String id) {
        return iNews.findById(id);
    }

    @GetMapping("/date/{date}")
    public List<com.bupt.memes.model.media.News> findByDate(@PathVariable String date) {
        return iNews.findByDate(date);
    }

    @GetMapping("/month/{month}/day/{day}")
    public List<com.bupt.memes.model.media.News> findByMMDD(@PathVariable String month, @PathVariable String day) {
        return iNews.findByMMDD(month, day);
    }

    @GetMapping("/page")
    public PageResult<com.bupt.memes.model.media.News> page(Integer pageSize, String lastID) {
        if (pageSize == null) {
            pageSize = 10;
        }
        return iNews.find(false, pageSize, lastID);
    }
}

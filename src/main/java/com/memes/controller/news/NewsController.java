package com.memes.controller.news;

import com.memes.model.common.PageResult;
import com.memes.model.news.News;
import com.memes.model.param.ListNewsRequest;
import com.memes.service.news.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("newsController")
@RequestMapping("/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class NewsController {
    final NewsService newsService;

    @GetMapping("")
    public PageResult<News> listNews(ListNewsRequest request) {
        return newsService.listNews(request);
    }

    @GetMapping("/id/{id}")
    public News findById(@PathVariable String id) {
        return newsService.findById(id);
    }

    @GetMapping("/month/{month}/day/{day}")
    public List<News> findByMMDD(@PathVariable String month, @PathVariable String day) {
        return newsService.findByMMDD(month, day);
    }

}

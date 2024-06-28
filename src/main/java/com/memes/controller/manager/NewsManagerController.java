package com.memes.controller.manager;

import com.memes.annotation.AuthRequired;
import com.memes.model.news.News;
import com.memes.service.news.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class NewsManagerController {

    final NewsService newsService;

    @PostMapping("")
    @AuthRequired
    public News addNews(String data, @RequestParam(required = false) MultipartFile coverImage) {
        return newsService.addNews(News.fromJson(data), coverImage);
    }

    @DeleteMapping("/{id}")
    @AuthRequired
    public Boolean deleteNews(@PathVariable String id) {
        return newsService.deleteNews(id);
    }

}

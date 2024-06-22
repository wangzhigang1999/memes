package com.bupt.memes.controller.manager;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.service.Interface.INews;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class News {

    final INews iNews;

    @PostMapping("")
    @AuthRequired
    public com.bupt.memes.model.media.News addNews(String data, @RequestParam(required = false) MultipartFile coverImage) {
        return iNews.addNews(com.bupt.memes.model.media.News.fromJson(data), coverImage);
    }

    @DeleteMapping("/{id}")
    @AuthRequired
    public Boolean deleteNews(@PathVariable String id) {
        return iNews.deleteNews(id);
    }

}

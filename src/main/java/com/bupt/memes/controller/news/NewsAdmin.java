package com.bupt.memes.controller.news;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.model.common.ResultData;
import com.bupt.memes.model.media.News;
import com.bupt.memes.service.Interface.INews;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.bupt.memes.util.Utils.convertTag;

@RestController
@RequestMapping("/admin/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class NewsAdmin {

    final INews iNews;


    @PostMapping("/add")
    @AuthRequired
    public ResultData<News> addNews(String data, @RequestParam(required = false) MultipartFile coverImage) {
        News news = News.fromJson(data);
        return ResultData.success(iNews.addNews(news, coverImage));
    }

    @PostMapping("/delete")
    @AuthRequired
    public ResultData<Boolean> deleteNews(String id) {
        return ResultData.success(iNews.deleteNews(id));
    }


    @PostMapping("/tag/add")
    @AuthRequired
    public ResultData<News> addTag(String newsId, String tag) {
        return ResultData.success(iNews.addTag(newsId, convertTag(tag)));
    }

    @PostMapping("/tag/remove")
    @AuthRequired
    public ResultData<News> removeTag(String newsId, String tag) {
        return ResultData.success(iNews.removeTag(newsId, convertTag(tag)));
    }
}

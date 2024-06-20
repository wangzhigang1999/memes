package com.bupt.memes.controller.news;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.model.common.ResultData;
import com.bupt.memes.model.media.News;
import com.bupt.memes.service.Interface.INews;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/news")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class NewsAdmin {

	final INews iNews;

	@PostMapping("")
	@AuthRequired
	public ResultData<News> addNews(String data, @RequestParam(required = false) MultipartFile coverImage) {
		News news = News.fromJson(data);
		return ResultData.success(iNews.addNews(news, coverImage));
	}

	@DeleteMapping("/{id}")
	@AuthRequired
	public Boolean deleteNews(@PathVariable String id) {
		return iNews.deleteNews(id);
	}

}

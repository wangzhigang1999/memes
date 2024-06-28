package com.memes.service.news;

import com.memes.model.common.PageResult;
import com.memes.model.news.News;
import com.memes.model.param.ListNewsRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NewsService {

    News addNews(News news, MultipartFile coverImage);

    News findById(String id);

    List<News> findByMMDD(String month, String day);

    boolean deleteNews(String id);

    PageResult<News> listNews(ListNewsRequest request);
}

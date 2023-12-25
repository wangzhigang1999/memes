package com.bupt.memes.service.Interface;

import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.model.media.News;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public interface INews {


    News addNews(News news, MultipartFile coverImage);


    News addTag(String newsId, Set<String> tag);

    News removeTag(String newsId, Set<String> tag);

    News findById(String id);

    List<News> findByDate(String date);

    List<News> findByMMDD(String month, String day);

    boolean deleteNews(String id);

    PageResult<News> find(boolean hasContent, int pageSize, String lastID);

    PageResult<News> findByTag(Set<String> tags, boolean hasContent, int pageSize, String lastID);
}

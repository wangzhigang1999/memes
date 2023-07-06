package com.bupt.dailyhaha.service.Interface;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.media.News;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface INews {


    News addNews(News news, MultipartFile coverImage);

    News addTag(String newsId, Set<String> tag);

    News removeTag(String newsId, Set<String> tag);

    News findById(String id);

    boolean deleteNews(String id);

    PageResult<News> find(int pageNum, int pageSize, String lastID);

    PageResult<News> findByTag(Set<String> tags, int pageNum, int pageSize, String lastID);
}

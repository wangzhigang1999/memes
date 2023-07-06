package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.media.News;
import com.bupt.dailyhaha.service.Interface.INews;
import com.bupt.dailyhaha.service.Interface.Storage;
import com.bupt.dailyhaha.service.MongoPageHelper;
import com.bupt.dailyhaha.util.Utils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class INewsImpl implements INews {
    final MongoTemplate mongoTemplate;
    final MongoPageHelper mongoPageHelper;

    final Storage storage;

    ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 0, TimeUnit.HOURS, new LinkedBlockingQueue<>());

    public INewsImpl(MongoTemplate mongoTemplate, MongoPageHelper mongoPageHelper, Storage storage) {
        this.mongoTemplate = mongoTemplate;
        this.mongoPageHelper = mongoPageHelper;
        this.storage = storage;
    }

    @Override
    public News addNews(News news, MultipartFile coverImage) {

        // use thread pool to upload image
        Future<String> future = pool.submit(new AsyncUpload(coverImage));


        // title content sourceURL must not null
        if (news.getTitle() == null || news.getContent() == null || news.getSourceURL() == null) {
            return null;
        }


        // if date is null, then set date to now
        if (news.getDate() == null) {
            // YYYY-MM-DD in Asia/Shanghai
            news.setDate(Utils.getYMD());
        }

        // set timestamp to now
        news.setTimestamp(System.currentTimeMillis());


        String url;
        try {
            url = future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            url = "https://bbs.bupt.site/shadiao/1688628801659-98ede151-98ba-44b2-b198-9e5bff028350.jpeg";
        }
        news.setCoverImage(url);

        return mongoTemplate.insert(news);
    }

    @Override
    public News addTag(String newsId, @NonNull Set<String> tag) {
        News news = findById(newsId);
        if (news == null) {
            return null;
        }
        if (news.getTag() == null) {
            news.setTag(tag);
        } else {
            news.getTag().addAll(tag);
        }
        return mongoTemplate.save(news);
    }

    @Override
    public News removeTag(String newsId, Set<String> tag) {
        News news = findById(newsId);
        if (news == null) {
            return null;
        }
        if (news.getTag() == null) {
            return news;
        } else {
            news.getTag().removeAll(tag);
        }
        return mongoTemplate.save(news);
    }

    @Override
    public News findById(String id) {
        News news = mongoTemplate.findById(id, News.class);
        if (news == null || news.isDeleted()) {
            return null;
        }
        return news;
    }

    @Override
    public boolean deleteNews(String id) {
        News news = findById(id);
        if (news == null) {
            return false;
        }
        // logic delete
        news.setDeleted(true);
        mongoTemplate.save(news);
        return true;
    }

    @Override
    public PageResult<News> find(int pageNum, int pageSize, String lastID) {
        Query query = new Query();
        return mongoPageHelper.pageQuery(query, News.class, pageSize,pageNum, lastID);
    }

    @Override
    public PageResult<News> findByTag(Set<String> tags, int pageNum, int pageSize, String lastID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tag").in(tags));
        return mongoPageHelper.pageQuery(query, News.class, pageNum, pageSize, lastID);
    }


    class AsyncUpload implements Callable<String> {
        final MultipartFile file;

        public AsyncUpload(MultipartFile file) {
            this.file = file;
        }

        @Override
        public String call() {
            try {
                return storage.store(file.getBytes(), file.getContentType()).getUrl();
            } catch (IOException e) {
                return "Default";
            }
        }
    }
}

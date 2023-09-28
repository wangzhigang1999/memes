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
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class INewsImpl implements INews {
    final MongoTemplate mongoTemplate;
    final MongoPageHelper mongoPageHelper;

    final Storage storage;

    String cacheKey = "";
    List<News> cache = null;

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
        String url = news.getCoverImage();
        try {
            url = future.get(10, TimeUnit.SECONDS);
        } catch (Exception ignored) {

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
    public List<News> findByDate(String date) {
        // if empty then set to today
        if (date == null || date.isEmpty()) {
            date = Utils.getYMD();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("date").is(date));
        query.addCriteria(Criteria.where("deleted").is(false));
        return mongoTemplate.find(query, News.class);
    }

    @Override
    public List<News> findByMMDD(String MMDD) {
        // if empty then set to today
        if (MMDD == null || MMDD.isEmpty()) {
            MMDD = Utils.getYMD().substring(5);
        }

        // if cache hit then return cache
        if (cacheKey.equals(MMDD)) {
            return cache;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("date").regex(MMDD));
        query.addCriteria(Criteria.where("deleted").is(false));
        List<News> news = mongoTemplate.find(query, News.class);

        cacheKey = MMDD;
        cache = news;
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
    public PageResult<News> find(int pageNum, boolean hasContent, int pageSize, String lastID) {
        Query query = new Query();
        // if hasContent is true, then return news with content
        if (!hasContent) {
            query.fields().exclude("content");
        }
        return mongoPageHelper.pageQuery(query, News.class, pageSize, pageNum, lastID);
    }

    @Override
    public PageResult<News> findByTag(Set<String> tags, boolean hasContent, int pageNum, int pageSize, String lastID) {
        Query query = new Query();
        if (!hasContent) {
            query.fields().exclude("content");
        }
        // tag query, the result must contain all tags
        query.addCriteria(Criteria.where("tag").all(tags));
        return mongoPageHelper.pageQuery(query, News.class, pageSize, pageNum, lastID);
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
                return "";
            }
        }
    }
}
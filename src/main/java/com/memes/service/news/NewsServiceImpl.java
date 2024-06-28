package com.memes.service.news;

import com.memes.config.AppConfig;
import com.memes.exception.AppException;
import com.memes.model.common.FileUploadResult;
import com.memes.model.common.PageResult;
import com.memes.model.news.News;
import com.memes.model.param.ListNewsRequest;
import com.memes.service.MongoPageHelper;
import com.memes.service.storage.StorageService;
import com.memes.util.FileUploader;
import com.memes.util.Preconditions;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.memes.util.TimeUtil.getYMD;

@Service
@Slf4j
@AllArgsConstructor
public class NewsServiceImpl implements NewsService {
    final MongoTemplate mongoTemplate;
    final MongoPageHelper mongoPageHelper;
    final StorageService storageService;
    final AppConfig appConfig;

    @Override
    @SneakyThrows
    public News addNews(News news, @Nullable MultipartFile coverImage) {
        if (coverImage != null) {
            /*
             * 上传封面图片，封面图片并不是必须的
             * 如果没有上传封面图片，则使用默认的封面图片或者 news 对象中的封面图片
             */
            FileUploadResult coverImg = new FileUploader(coverImage, storageService).upload();
            news.setCoverImage(coverImg.url());
        }
        Preconditions.checkArgument(news != null && news.validate(), AppException.invalidParam("news"));
        if (news.getDate() == null) {
            news.setDate(getYMD());
        }
        news.setTimestamp(System.currentTimeMillis());
        return mongoTemplate.insert(news);
    }

    @Override
    public News findById(String id) {
        Preconditions.checkArgument(id != null && !id.isEmpty(), AppException.invalidParam("id"));
        News news = mongoTemplate.findById(id, News.class);
        Preconditions.checkArgument(news != null, AppException.resourceNotFound("news"));
        Preconditions.checkArgument(!news.isDeleted(), AppException.resourceNotFound("news"));
        return news;
    }

    @Override
    public List<News> findByMMDD(String month, String day) {
        Preconditions.checkArgument(month != null && day != null, AppException.invalidParam("date"));
        month = String.format("%02d", Integer.parseInt(month));
        day = String.format("%02d", Integer.parseInt(day));
        // 确保匹配的是 YYYY-MM-DD 格式
        String monthAndDay = ".*-%s-%s".formatted(month, day);
        Query query = new Query();
        query.addCriteria(Criteria.where("date").regex(monthAndDay));
        query.addCriteria(Criteria.where("deleted").is(false));
        return mongoTemplate.find(query, News.class);
    }

    @Override
    public boolean deleteNews(String id) {
        News news = findById(id);
        Preconditions.checkArgument(news != null, AppException.resourceNotFound("news"));
        news.setDeleted(true);
        mongoTemplate.save(news);
        return true;
    }

    @Override
    public PageResult<News> listNews(ListNewsRequest request) {
        Preconditions.checkArgument(request.getPageSize() > 0, AppException.invalidParam("pageSize"));
        var limit = Math.min(request.getPageSize(), appConfig.newsFetchLimit);
        Query query = new Query();
        addCriteriaIfNotBlank(query, "author", request.getAuthor(), false);
        addCriteriaIfNotBlank(query, "title", request.getTitle(), true);
        addCriteriaIfNotBlank(query, "introduction", request.getIntroduction(), true);
        addCriteriaIfNotBlank(query, "content", request.getContent(), true);
        addCriteriaIfNotBlank(query, "date", request.getDate(), false);
        if (!CollectionUtils.isEmpty(request.getTag())) {
            query.addCriteria(Criteria.where("tag").all(request.getTag()));
        }
        query.fields().exclude("content");
        query.addCriteria(Criteria.where("deleted").is(false));
        return mongoPageHelper.pageQuery(query, News.class, limit, request.getLastID());
    }

    private void addCriteriaIfNotBlank(Query query, String field, String value, boolean regex) {
        if (StringUtils.isNotBlank(value)) {
            query.addCriteria(regex ? Criteria.where(field).regex(value) : Criteria.where(field).is(value));
        }
    }

}

package com.bupt.memes.service.impl;

import com.bupt.memes.model.common.FileUploadResult;
import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.model.media.News;
import com.bupt.memes.service.Interface.INews;
import com.bupt.memes.service.Interface.Storage;
import com.bupt.memes.service.MongoPageHelper;
import com.bupt.memes.util.FileUploader;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static com.bupt.memes.util.TimeUtil.getYMD;

@Service
public class INewsImpl implements INews {
	final MongoTemplate mongoTemplate;
	final MongoPageHelper mongoPageHelper;
	final Storage storage;

	final static Logger logger = org.slf4j.LoggerFactory.getLogger(INewsImpl.class);

	public INewsImpl(MongoTemplate mongoTemplate, MongoPageHelper mongoPageHelper, Storage storage) {
		this.mongoTemplate = mongoTemplate;
		this.mongoPageHelper = mongoPageHelper;
		this.storage = storage;
	}

	@Override
	@SneakyThrows
	public News addNews(News news, @Nullable MultipartFile coverImage) {
		if (coverImage != null) {
			/*
			 * 上传封面图片，封面图片并不是必须的
			 * 如果没有上传封面图片，则使用默认的封面图片或者 news 对象中的封面图片
			 */
			FileUploadResult coverImg = new FileUploader(coverImage, storage).upload();
			news.setCoverImage(coverImg.url());
		}
		if (!news.validate()) {
			logger.warn("add news failed, news invalid, news: {}", news);
			return null;
		}
		if (news.getDate() == null) {
			news.setDate(getYMD());
		}
		news.setTimestamp(System.currentTimeMillis());
		return mongoTemplate.insert(news);
	}

	@Override
	public News addTag(String newsId, Set<String> tag) {
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
		// if empty, then set to today
		if (date == null || date.isEmpty()) {
			date = getYMD();
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("date").is(date));
		query.addCriteria(Criteria.where("deleted").is(false));
		return mongoTemplate.find(query, News.class);
	}

	@Override
	public List<News> findByMMDD(String month, String day) {

		month = String.format("%02d", Integer.parseInt(month));
		day = String.format("%02d", Integer.parseInt(day));

		// 确保匹配的是 YYYY-MM-DD 格式
		String monthAndDay = ".*-" + month + "-" + day;

		Query query = new Query();
		query.addCriteria(Criteria.where("date").regex(monthAndDay));
		query.addCriteria(Criteria.where("deleted").is(false));

		return mongoTemplate.find(query, News.class);
	}

	@Override
	public boolean deleteNews(String id) {
		News news = findById(id);
		if (news == null) {
			logger.warn("delete news failed, news not found, id: {}", id);
			return false;
		}
		// logic delete
		news.setDeleted(true);
		mongoTemplate.save(news);
		return true;
	}

	@Override
	public PageResult<News> find(boolean hasContent, int pageSize, String lastID) {
		Query query = new Query();
		// if hasContent is true, then return news with content
		if (!hasContent) {
			query.fields().exclude("content");
		}
		query.addCriteria(Criteria.where("deleted").is(false));
		return mongoPageHelper.pageQuery(query, News.class, pageSize, lastID);
	}

	@Override
	public PageResult<News> findByTag(Set<String> tags, boolean hasContent, int pageSize, String lastID) {
		Query query = new Query();
		if (!hasContent) {
			query.fields().exclude("content");
			query.addCriteria(Criteria.where("deleted").is(false));
		}
		// tag query, the result must contain all tags
		query.addCriteria(Criteria.where("tag").all(tags));
		return mongoPageHelper.pageQuery(query, News.class, pageSize, lastID);
	}

}

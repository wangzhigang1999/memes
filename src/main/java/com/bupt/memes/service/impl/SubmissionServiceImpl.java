package com.bupt.memes.service.impl;

import com.bupt.memes.aspect.Audit;
import com.bupt.memes.model.HNSWItem;
import com.bupt.memes.model.common.FileUploadResult;
import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.AnnIndexService;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.service.Interface.Storage;
import com.bupt.memes.service.MongoPageHelper;
import com.bupt.memes.service.SysConfigService;
import com.bupt.memes.util.KafkaUtil;
import com.bupt.memes.util.TimeUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.jelmerk.knn.SearchResult;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bupt.memes.model.common.SubmissionCollection.*;

@Service
@AllArgsConstructor
@SuppressWarnings("null")
public class SubmissionServiceImpl implements ISubmission {

	final MongoTemplate mongoTemplate;
	final MongoPageHelper mongoPageHelper;
	final SysConfigService sysConfigService;
	final AnnIndexService annIndexService;

	final Storage storage;
	final static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
	final static Logger logger = LogManager.getLogger(SubmissionServiceImpl.class);

	private static volatile Cache<String, Submission> submissionCache;

	/**
	 * 对投稿点赞/点踩
	 *
	 * @param id
	 *            对应投稿的 id
	 * @param up
	 *            true 为点赞，false 为点踩
	 * @return 是否成功
	 */
	@Override
	public boolean vote(String id, boolean up) {
		var query = new Query(Criteria.where("id").is(id));
		var update = new Update();
		update.inc(up ? "up" : "down", 1);
		UpdateResult first = mongoTemplate.update(Submission.class).matching(query).apply(update).first();
		return first.getMatchedCount() > 0;
	}

	/**
	 * 存储文本类型的投稿
	 *
	 * @param text
	 *            url text
	 * @param mime
	 *            mime
	 * @return 存储后的投稿
	 */
	@Override
	public Submission storeTextFormatSubmission(String text, String mime) {
		var submission = getSubmission(text.hashCode());
		if (submission != null) {
			return submission;
		}
		submission = new Submission().setSubmissionType(mime);
		if (!formatDetected(text, submission)) {
			logger.error("format not detected, text: {},mime: {}", text, mime);
			return null;
		}
		Submission inserted = insertSubmission(submission);
		if (inserted == null) {
			return null;
		}
		KafkaUtil.sendTextSubmission(inserted.getId(), text);
		return inserted;
	}

	private static boolean formatDetected(String text, Submission submission) {
		switch (submission.getSubmissionType()) {
			case BILIBILI:
				submission.setName(text);
				submission.setUrl(text);
				break;
			case MARKDOWN:
				if (text.startsWith("http")) {
					submission.setUrl(text);
					submission.setName(text);
				} else {
					submission.setContent(text);
					submission.setName("markdown");
				}
				break;
			case VIDEO:
				submission.setUrl(text);
				submission.setName(text);
				break;
			default:
				return false;
		}
		submission.setHash(text.hashCode());
		return true;
	}

	/**
	 * 存储二进制类型的投稿
	 *
	 * @param stream
	 *            输入流
	 * @param mime
	 *            mime
	 * @return 存储后的投稿
	 */
	@Override
	@SneakyThrows
	public Submission storeStreamSubmission(InputStream stream, String mime) {
		byte[] bytes = stream.readAllBytes();
		int code = Arrays.hashCode(bytes);

		CompletableFuture<FileUploadResult> upload = CompletableFuture.supplyAsync(() -> storage.store(bytes, mime),
				executor);
		CompletableFuture<Submission> query = CompletableFuture.supplyAsync(() -> getSubmission(code), executor);
		/*
		 * 从经验来看，queryFuture.get() 会比较快，所以先尝试从数据库中查询
		 * 如果查询到了，直接返回
		 */
		Submission submission = query.get();
		if (submission != null) {
			upload.cancel(true);
			logger.info("get submission from db,cancel upload. hash: {}", code);
			return submission;
		}
		/*
		 * 如果没有查询到，那么等待存储结果
		 */
		FileUploadResult result = upload.get();
		if (result == null) {
			return null;
		}
		submission = new Submission();
		submission.setHash(code).setSubmissionType(mime).setUrl(result.url()).setName(result.fileName());
		Submission inserted = insertSubmission(submission);
		if (inserted == null) {
			return null;
		}
		KafkaUtil.sendBinarySubmission(inserted.getId(), bytes);
		return inserted;
	}

	/**
	 * 获取被标记为删除的投稿
	 */
	@Override
	public List<Submission> getDeletedSubmission() {
		return mongoTemplate.findAll(Submission.class, DELETED_SUBMISSION);
	}

	/**
	 * 标记删除，只有通过审核的投稿才能被标记删除，所以不用考虑 WAITING_SUBMISSION
	 *
	 * @param id
	 *            投稿 id
	 * @return 是否成功
	 */
	@Transactional
	@Override
	public boolean markDelete(String id) {
		Submission submission = getSubmissionById(id);
		if (submission == null) {
			return false;
		}
		mongoTemplate.save(submission, DELETED_SUBMISSION);
		mongoTemplate.remove(submission, SUBMISSION);
		// todo: 删除索引,删除缓存
		return true;
	}

	/**
	 * 硬删除
	 */
	@Override
	public void hardDeleteSubmission(String id) {
		mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), Submission.class, DELETED_SUBMISSION);
	}

	/**
	 * 分页查询
	 *
	 * @param pageSize
	 *            每页大小
	 * @param lastID
	 *            上一页最后一个元素的 id
	 * @return 分页结果
	 */
	@Override
	public PageResult<Submission> getSubmissionByPage(int pageSize, String lastID) {
		logger.debug("get submission from db, lastID: {}", Objects.equals(lastID, "") ? "null" : lastID);
		return mongoPageHelper.pageQuery(new Query(), Submission.class, pageSize, lastID);
	}

	@Override
	public Submission getSubmissionById(String id) {
		if (submissionCache == null) {
			synchronized (SubmissionServiceImpl.class) {
				if (submissionCache == null) {
					submissionCache = Caffeine.newBuilder().maximumSize(sysConfigService.getSubmissionCacheSize()).build();
					logger.info("init cache, submission cache size: {}", sysConfigService.getSubmissionCacheSize());
				}
			}
		}
		// 在 submission 这个 collection 中的 id 是唯一的，并且每一个记录是不可变且不会被删除的
		return submissionCache.get(id, k -> mongoTemplate.findById(k, Submission.class));
	}

	@Override
	public List<Submission> getSubmissionByDate(String date) {
		long start = TimeUtil.convertYMDToUnixEpochMilli(date);
		// subtract 2 hours
		start -= 2 * 60 * 60 * 1000;
		long end = start + 24 * 60 * 60 * 1000;
		// time 上必须加索引
		var time = "timestamp";
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(
				Criteria.where(time).gte(start),
				Criteria.where(time).lt(end)));
		return mongoTemplate.find(query, Submission.class);
	}

	@Override
	public List<Submission> getSimilarSubmission(String id, int size) {
		size = Math.min(sysConfigService.getTopK(), size);
		List<SearchResult<HNSWItem, Float>> search = annIndexService.search(id, size);
		return getSubmissionsByHNSWItems(search);
	}

	@Override
	public List<Submission> randomSubmission(int size) {
		float[] vector = new float[768];
		for (int i = 0; i < 768; i++) {
			vector[i] = (float) Math.random();
		}
		size = Math.min(sysConfigService.getTopK(), size);
		List<SearchResult<HNSWItem, Float>> search = annIndexService.search(vector, size);
		return getSubmissionsByHNSWItems(search);
	}

	/**
	 * 插入投稿
	 *
	 * @param submission
	 *            投稿
	 * @return 插入后的投稿
	 */
	private Submission insertSubmission(Submission submission) {
		// 默认情况下，往 WAITING_SUBMISSION 中插入
		String uuid = Audit.threadLocalUUID.get();
		submission.setUploader(uuid);
		try {
			mongoTemplate.save(submission, WAITING_SUBMISSION);
		} catch (DuplicateKeyException e) {
			submission = mongoTemplate.findOne(
					Query.query(Criteria.where("hash").is(submission.getHash())),
					Submission.class,
					WAITING_SUBMISSION);
		}
		return submission;
	}

	@SneakyThrows
	@SuppressWarnings({ "unchecked" })
	private Submission getSubmission(Integer hash) {
		CompletableFuture<Submission>[] futures = COLLECTIONS.stream()
				.map(collection -> CompletableFuture
						.supplyAsync(() -> mongoTemplate.findOne(Query.query(Criteria.where("hash").is(hash)),
								Submission.class, collection)))
				.toArray(CompletableFuture[]::new);

		return CompletableFuture
				.allOf(futures)
				.thenApply(ignored -> Arrays.stream(futures)
						.map(CompletableFuture::join)
						.filter(Objects::nonNull)
						.findFirst()
						.orElse(null))
				.get();

	}

	private List<Submission> getSubmissionsByHNSWItems(List<SearchResult<HNSWItem, Float>> search) {
		List<CompletableFuture<Submission>> list = search.stream()
				.map(SearchResult::item)
				.map(HNSWItem::getId)
				.map(k -> CompletableFuture.supplyAsync(() -> getSubmissionById(k), executor))
				.toList();
		return list.stream().map(CompletableFuture::join)
				.filter(Objects::nonNull)
				.distinct()
				.toList();
	}

}

package com.memes.service.submission;

import com.memes.aspect.Audit;
import com.memes.config.AppConfig;
import com.memes.exception.AppException;
import com.memes.model.common.FileUploadResult;
import com.memes.model.common.PageResult;
import com.memes.model.param.ListSubmissionsRequest;
import com.memes.model.submission.Submission;
import com.memes.service.MessageQueueService;
import com.memes.service.MongoPageHelper;
import com.memes.service.storage.StorageService;
import com.memes.util.Preconditions;
import com.memes.util.TimeUtil;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.UpdateResult;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.alibaba.dashscope.utils.JsonUtils.gson;
import static com.memes.model.common.SubmissionCollection.*;

@Service
@AllArgsConstructor
@SuppressWarnings("null")
public class SubmissionServiceImpl implements SubmissionService {

    final MongoTemplate mongoTemplate;
    final MongoPageHelper mongoPageHelper;
    final AppConfig appConfig;

    final MessageQueueService mqService;
    final StorageService storageService;
    final RedisTemplate<String, String> redisTemplate;
    final static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    final static Logger logger = LogManager.getLogger(SubmissionServiceImpl.class);

    private final Set<String> feedbacks = Set.of("like", "dislike");

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
        Preconditions.checkArgument(formatDetected(text, submission), AppException.invalidParam("format"));
        Submission inserted = insertSubmission(submission);
        Preconditions.checkNotNull(inserted, AppException.internalError("insert submission failed"));
        Long memes = mqService.sendMessage("memes", submission);
        Preconditions.checkNotNull(memes, AppException.internalError("send message to mq failed"));
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

        var upload = CompletableFuture.supplyAsync(() -> storageService.store(bytes, mime), executor);
        var query = CompletableFuture.supplyAsync(() -> getSubmission(code), executor);
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
        Preconditions.checkNotNull(result, AppException.internalError("please try again later."));
        submission = new Submission();
        submission.setHash(code).setSubmissionType(mime).setUrl(result.url()).setName(result.fileName());
        Submission inserted = insertSubmission(submission);
        Preconditions.checkNotNull(inserted, AppException.databaseError(""));
        Long memes = mqService.sendMessage("memes", submission);
        Preconditions.checkNotNull(memes, AppException.internalError("send message to mq failed"));
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
        Preconditions.checkNotNull(submission, AppException.resourceNotFound("submission"));
        mongoTemplate.save(submission, DELETED_SUBMISSION);
        mongoTemplate.remove(submission, PASSED_SUBMISSION);
        return true;
    }

    /**
     * 硬删除
     */
    @Override
    public void hardDelete(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), Submission.class, DELETED_SUBMISSION);
    }

    @Override
    public Submission getSubmissionById(String id) {
        String submission = (String) redisTemplate.opsForHash().get("submission", id);
        if (submission == null || submission.isEmpty()) {
            return mongoTemplate.findById(id, Submission.class);
        }
        return gson.fromJson(submission, Submission.class);
    }

    @Override
    public List<Submission> getSimilarSubmission(String id, int size) {
        return List.of();
        // Preconditions.checkArgument(size > 0, AppException.invalidParam("size"));
        // size = Math.min(appConfig.topK, size);
        // List<SearchResult<HNSWItem, Float>> search = annIndexService.search(id,
        // size);
        // return getSubmissionsByHNSWItems(search);
    }

    @Override
    public boolean feedback(String id, String feedback) {
        Preconditions.checkArgument(feedbacks.contains(feedback), AppException.invalidParam("feedback"));
        var query = new Query(Criteria.where("id").is(id));
        var update = new Update();
        update.inc(feedback, 1);
        UpdateResult first = mongoTemplate.update(Submission.class).matching(query).apply(update).first();
        return first.getMatchedCount() > 0;
    }

    @Override
    public PageResult<Submission> listSubmissions(ListSubmissionsRequest request) {
        String date = request.getDate();
        int pageSize = request.getPageSize();
        String lastId = request.getLastID();
        Preconditions.checkArgument(pageSize > 0, AppException.invalidParam("pageSize"));
        pageSize = Math.min(pageSize, appConfig.subFetchLimit);
        Query query = new Query();
        if (StringUtils.isNotBlank(date)) {
            long start = TimeUtil.convertYMDToUnixEpochMilli(date);
            start -= 2 * 60 * 60 * 1000;
            long end = start + 24 * 60 * 60 * 1000;
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("timestamp").gte(start),
                    Criteria.where("timestamp").lt(end)));
        }
        return mongoPageHelper.pageQuery(query, Submission.class, pageSize, lastId);
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

}

package com.bupt.memes.service.impl;

import com.bupt.memes.aspect.Audit;
import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.service.Interface.Storage;
import com.bupt.memes.service.MongoPageHelper;
import com.bupt.memes.util.TimeUtil;
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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.bupt.memes.model.common.SubmissionCollection.*;

@Service
@AllArgsConstructor
public class SubmissionServiceImpl implements ISubmission {

    final MongoTemplate mongoTemplate;
    final MongoPageHelper mongoPageHelper;

    final Storage storage;
    final static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    final static Logger logger = LogManager.getLogger(SubmissionServiceImpl.class);


    /**
     * 对投稿点赞/点踩
     *
     * @param id 对应投稿的id
     * @param up true为点赞，false为点踩
     * @return 是否成功
     */
    @Override
    public boolean vote(String id, boolean up) {
        var query = new Query(Criteria.where("id").is(id));
        var update = new Update();
        if (up) {
            update.inc("up", 1);
        } else {
            update.inc("down", 1);
        }
        UpdateResult first = mongoTemplate.update(Submission.class).matching(query).apply(update).first();

        return first.getMatchedCount() > 0;
    }


    /**
     * 存储文本类型的投稿
     *
     * @param text url  text
     * @param mime mime
     * @return 存储后的投稿
     */
    @Override
    public Submission storeTextFormatSubmission(String text, String mime) {

        // check if the submission already exists
        var submission = getSubmission(text.hashCode());
        if (submission != null) {
            return submission;
        }

        submission = new Submission();
        submission.setSubmissionType(mime);
        submission.setHash(text.hashCode());


        switch (submission.getSubmissionType()) {
            case BILIBILI:
                submission.setName(text);
                submission.setUrl(text);
                break;
            case MARKDOWN:
                // if the text is an url, then set the url
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
                return null;
        }


        return insertSubmission(submission);
    }

    /**
     * 存储二进制类型的投稿
     *
     * @param stream 输入流
     * @param mime   mime
     * @return 存储后的投稿
     */
    @Override
    @SneakyThrows
    public Submission storeStreamSubmission(InputStream stream, String mime) {
        byte[] bytes = stream.readAllBytes();

        int code = Arrays.hashCode(bytes);

        CountDownLatch latch = new CountDownLatch(2);
        CountDownLatch shortcut = new CountDownLatch(1);

        AtomicReference<Submission> uploaded = new AtomicReference<>();
        AtomicReference<Submission> inDB = new AtomicReference<>();

        // async store; 开启一个协程直接上传
        executor.submit(() -> {
            Submission store = storage.store(bytes, mime);
            uploaded.set(store);
            latch.countDown();
        });

        // async query; 开启一个协程直接查询
        executor.submit(() -> {
            var submission = getSubmission(code);
            inDB.set(submission);
            latch.countDown();
            shortcut.countDown();
        });

        // 短路一下，如果数据库中先查到了，就直接返回
        shortcut.await();
        if (inDB.get() != null) {
            return inDB.get();
        }

        // 数据库中没有，等待上传完成
        latch.await();


        if (uploaded.get() == null) {
            return null;
        }
        var submission = uploaded.get().setHash(code);
        return insertSubmission(submission);
    }

    /**
     * 获取被标记为删除的投稿
     */
    @Override
    public List<Submission> getDeletedSubmission() {
        return mongoTemplate.findAll(Submission.class, DELETED_SUBMISSION);
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
     * @param pageSize 每页大小
     * @param lastID   上一页最后一个元素的id
     * @return 分页结果
     */
    @Override
    public PageResult<Submission> getSubmissionByPage(int pageSize, String lastID) {
        logger.info("get submission from db, lastID: {}", Objects.equals(lastID, "") ? "null" : lastID);
        return mongoPageHelper.pageQuery(new Query(), Submission.class, pageSize, lastID);
    }

    @Override
    public Submission getSubmissionById(String id) {
        return mongoTemplate.findById(id, Submission.class);
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
                Criteria.where(time).lt(end)
        ));
        return mongoTemplate.find(query, Submission.class);
    }

    /**
     * 插入投稿
     *
     * @param submission 投稿
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
                    WAITING_SUBMISSION
            );
        }
        return submission;
    }

    @SneakyThrows
    @SuppressWarnings({"unchecked"})
    private Submission getSubmission(Integer hash) {
        CompletableFuture<Submission>[] futures = COLLECTIONS.stream()
                .map(collection -> CompletableFuture
                        .supplyAsync(() -> mongoTemplate.findOne(Query.query(Criteria.where("hash").is(hash)), Submission.class, collection)))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture
                .allOf(futures)
                .thenApply(ignored -> Arrays.stream(futures)
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)).get();

    }


}

package com.bupt.memes.service.impl;

import com.bupt.memes.aspect.Audit;
import com.bupt.memes.pojo.common.PageResult;
import com.bupt.memes.pojo.media.Submission;
import com.bupt.memes.service.Interface.ISubmission;
import com.bupt.memes.service.Interface.Storage;
import com.bupt.memes.service.MongoPageHelper;
import com.bupt.memes.util.Utils;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class SubmissionServiceImpl implements ISubmission {

    final MongoTemplate mongoTemplate;
    final MongoPageHelper mongoPageHelper;

    final Storage storage;
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(SubmissionServiceImpl.class);

    final static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();


    /**
     * 对投稿点赞/点踩
     *
     * @param hashcode 对应投稿的id
     * @param up       true为点赞，false为点踩
     * @return 是否成功
     */
    @Override
    public boolean vote(int hashcode, boolean up) {
        // if up is true, then vote up, else vote down
        var query = new Query(Criteria.where("hash").is(hashcode));
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
     * @param uri  url  text
     * @param mime mime
     * @return 存储后的投稿
     */
    @Override
    public Submission storeTextFormatSubmission(String uri, String mime) {

        // check if the submission already exists
        var submission = mongoTemplate.findOne(Query.query(Criteria.where("hash").is(uri.hashCode())), Submission.class);
        if (submission != null) {
            return submission;
        }

        submission = new Submission();
        submission.setSubmissionType(mime);
        submission.setName(uri);
        submission.setUrl(uri);
        submission.setHash(uri.hashCode());

        return insertSubmission(submission);
    }

    /**
     * 存储二进制类型的投稿
     *
     * @param stream   输入流
     * @param mime     mime
     * @param personal 是否私有
     * @return 存储后的投稿
     */
    @Override
    @SneakyThrows
    public Submission storeStreamSubmission(InputStream stream, String mime, boolean personal) {
        byte[] bytes = Utils.readAllBytes(stream);

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
            var submission = mongoTemplate.findOne(Query.query(Criteria.where("hash").is(code)), Submission.class);
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
        return mongoTemplate.find(Query.query(Criteria.where("deleted").is(true)), Submission.class);
    }

    /**
     * 硬删除
     */
    @Override
    public void hardDeleteSubmission(int hashcode) {
        mongoTemplate.remove(Query.query(Criteria.where("hash").is(hashcode)), Submission.class);
    }

    /**
     * 分页查询
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param lastID   上一页最后一个元素的id
     * @return 分页结果
     */
    @Override
    @Cacheable(value = "submission", key = "#lastID + #pageNum + #pageSize",condition = "#lastID != null && #lastID !=''")
    public PageResult<Submission> getSubmissionByPage(int pageNum, int pageSize, String lastID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("reviewed").is(true));
        return mongoPageHelper.pageQuery(query, Submission.class, pageSize, pageNum, lastID);
    }

    /**
     * 插入投稿
     *
     * @param submission 投稿
     * @return 插入后的投稿
     */
    private Submission insertSubmission(Submission submission) {
        String uuid = Audit.threadLocalUUID.get();
        submission.setUploader(uuid);
        try {
            mongoTemplate.save(submission);
        } catch (DuplicateKeyException e) {
            logger.info("duplicate submission, hash: {}", submission.getHash());
            submission = mongoTemplate.findOne(Query.query(Criteria.where("hash").is(submission.getHash())), Submission.class);
        }
        return submission;
    }
}

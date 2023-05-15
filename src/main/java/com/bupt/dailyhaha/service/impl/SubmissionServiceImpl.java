package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.PageResult;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.MongoPageHelper;
import com.bupt.dailyhaha.service.Storage;
import com.bupt.dailyhaha.service.SubmissionService;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    final MongoTemplate mongoTemplate;
    final MongoPageHelper mongoPageHelper;

    final Storage storage;

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(SubmissionServiceImpl.class);
    final static ConcurrentHashMap<Integer, Submission> cache = new ConcurrentHashMap<>();

    public SubmissionServiceImpl(MongoTemplate mongoTemplate, MongoPageHelper mongoPageHelper, Storage storage) {
        this.mongoTemplate = mongoTemplate;
        this.mongoPageHelper = mongoPageHelper;
        this.storage = storage;
    }




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


    @Override
    public Submission storeTextFormatSubmission(String uri, String mime) {

        // check if the submission already exists
        Submission submission = mongoTemplate.findOne(Query.query(Criteria.where("hash").is(uri.hashCode())), Submission.class);
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

    @Override
    public Submission storeStreamSubmission(InputStream stream, String mime, boolean personal) {
        byte[] bytes = Utils.readAllBytes(stream);
        if (bytes == null) {
            return null;
        }
        int code = Arrays.hashCode(bytes);
        if (cache.containsKey(code)) {
            return cache.get(code);
        }

        // check if the submission already exists
        // 为什么需要这个？因为Pod会重启，重启之后会丢失缓存的信息，所以需要从数据库中查询
        // 也可以使用redis来做缓存，但是这个项目没有必要
        Submission submission = mongoTemplate.findOne(Query.query(Criteria.where("hash").is(code)), Submission.class);
        if (submission != null) {
            cache.put(code, submission);
            return submission;
        }

        submission = storage.store(bytes, mime);
        if (submission == null) {
            return null;
        }
        submission.setHash(code);

        // 当做图床用的时候，不入库
        if (personal) {
            return submission;
        }

        cache.put(code, insertSubmission(submission));
        return submission;
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

    @Override
    public PageResult<Submission> getSubmissionByPage(int pageNum, int pageSize, String lastID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("reviewed").is(true));
        return mongoPageHelper.pageQuery(query, Submission.class, pageSize, pageNum, lastID);
    }


    private Submission insertSubmission(Submission submission) {
        try {
            mongoTemplate.save(submission);
        } catch (DuplicateKeyException e) {
            logger.info("duplicate submission, hash: {}", submission.getHash());
            submission = mongoTemplate.findOne(Query.query(Criteria.where("hash").is(submission.getHash())), Submission.class);
        }
        return submission;
    }
}

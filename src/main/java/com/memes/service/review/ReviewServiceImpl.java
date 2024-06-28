package com.memes.service.review;

import com.memes.exception.AppException;
import com.memes.model.submission.Submission;
import com.memes.util.Preconditions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.comparator.Comparators;

import java.util.List;
import java.util.Set;

import static com.memes.model.common.SubmissionCollection.*;
import static com.memes.util.TimeUtil.getTodayStartUnixEpochMilli;

/**
 * 审核相关的服务
 * 对于所有的投稿，都要人工的进行审核
 */
@Service
@AllArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    final MongoTemplate template;

    final Set<String> validOperations = Set.of("accept", "reject");

    @Override
    public List<Submission> listWaitingSubmission(Integer limit) {
        if (limit == null) {
            List<Submission> submissions = template.findAll(Submission.class, WAITING_SUBMISSION);
            submissions.sort(Comparators.comparable());
            return submissions;
        }
        Preconditions.checkArgument(limit > 0, AppException.invalidParam("limit"));
        var query = new Query();
        query.limit(limit);
        query.with(Sort.by(Sort.Direction.ASC, "timestamp"));
        return template.find(query, Submission.class, WAITING_SUBMISSION);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchReviewSubmission(List<String> ids, String operation) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(ids), AppException.invalidParam("ids"));
        Preconditions.checkArgument(validOperations.contains(operation), AppException.invalidParam("operation"));
        boolean accept = operation.equals("accept");
        ids.forEach(id -> reviewSubmission(id, accept));
        return ids.size();
    }

    @Override
    public long getStatusNum(String status) {
        Preconditions.checkNotNull(status, AppException.invalidParam("status"));
        Preconditions.checkArgument(COLLECTIONS.contains(status), AppException.invalidParam("status"));
        // 00:00:00 of today
        var start = getTodayStartUnixEpochMilli();
        // 向前推两个小时，从上一天的 22 点开始算起
        var from = start - 2 * 60 * 60 * 1000;
        Criteria criteria = Criteria.where("timestamp").gte(from);
        return template.count(Query.query(criteria), Submission.class, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewSubmission(String id, String operation) {
        Preconditions.checkArgument(id != null, AppException.invalidParam("id"));
        Preconditions.checkArgument(validOperations.contains(operation), AppException.invalidParam("operation"));
        return reviewSubmission(id, operation.equals("accept"));
    }

    /**
     * 更新投稿的审核状态
     *
     * @param id
     *            投稿的 id
     * @param passed
     *            是否通过
     * @return 是否成功
     */
    private boolean reviewSubmission(String id, boolean passed) {
        Preconditions.checkArgument(id != null, AppException.invalidParam("id"));
        var query = new Query(Criteria.where("id").is(id));
        // 当一个投稿被审核通过或者不通过的时候，就从等待审核的表中删除，然后插入到对应的表中
        var submission = template.findAndRemove(query, Submission.class, WAITING_SUBMISSION);
        Preconditions.checkNotNull(submission, AppException.internalError("Failed to find submission with id: %s".formatted(id)));
        template.save(submission, passed ? PASSED_SUBMISSION : DELETED_SUBMISSION);
        return true;
    }

}

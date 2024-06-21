package com.bupt.memes.service.impl;

import com.bupt.memes.model.media.Submission;
import com.bupt.memes.model.media.SubmissionGroup;
import com.bupt.memes.model.media.SubmissionType;
import com.bupt.memes.service.Interface.ISubGroup;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.bupt.memes.model.common.SubmissionCollection.WAITING_SUBMISSION;

@Service
@AllArgsConstructor
@SuppressWarnings("null")
public class SubGroupImpl implements ISubGroup {

    final MongoTemplate template;

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(SubGroupImpl.class);

    @Override
    @Transactional
    public SubmissionGroup createGroup(List<String> submissionIds) {
        List<Submission> submissions = ensureSubmissionsExist(submissionIds);
        if (submissions.size() != submissionIds.size()) {
            logger.error("create image group failed,some submissions not exist");
            return null;
        }
        Optional<SubmissionGroup> group = SubmissionGroup.fromSubmission(submissions);
        assert group.isPresent();
        SubmissionGroup submissionGroup = group.get();
        List<Submission> images = submissionGroup.getChildren();
        images.forEach(submission -> template.remove(submission, WAITING_SUBMISSION));
        template.insert(submissionGroup, WAITING_SUBMISSION);
        return submissionGroup;
    }

    @Override
    @Transactional
    public SubmissionGroup addToGroup(String groupId, List<String> submissionIds) {
        SubmissionGroup submissionGroup = getById(groupId);
        if (submissionGroup == null) {
            logger.error("add image to group failed, group:{} not exist", groupId);
            return null;
        }
        List<Submission> submissions = ensureSubmissionsExist(submissionIds);
        if (submissions.size() != submissionIds.size()) {
            logger.error("add image to group:{} failed,some submissions not exist", groupId);
            return null;
        }
        // 不能合并两个 group
        if (submissions.stream().anyMatch(submission -> submission.getSubmissionType() == SubmissionType.BATCH)) {
            logger.error("add image to group failed,can not add group to group:{}", groupId);
            return null;
        }
        submissionGroup.addSubmissions(submissions);
        template.save(submissionGroup, WAITING_SUBMISSION);
        submissions.forEach(submission -> template.remove(submission, WAITING_SUBMISSION));
        return submissionGroup;
    }

    @Override
    public SubmissionGroup getById(String id) {
        return template.findById(id, SubmissionGroup.class, WAITING_SUBMISSION);
    }

    /**
     * 确保所有的 submission 都存在 waitingSubmission 中
     *
     * @param submissionsId
     *            submission id 列表
     * @return 存在的 submission 列表
     */
    private List<Submission> ensureSubmissionsExist(List<String> submissionsId) {
        return template.find(Query.query(Criteria.where("id").in(submissionsId)), Submission.class, WAITING_SUBMISSION);
    }
}

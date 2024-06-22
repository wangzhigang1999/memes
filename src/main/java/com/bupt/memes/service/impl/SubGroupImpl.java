package com.bupt.memes.service.impl;

import com.bupt.memes.exception.AppException;
import com.bupt.memes.model.media.Submission;
import com.bupt.memes.model.media.SubmissionGroup;
import com.bupt.memes.model.media.SubmissionType;
import com.bupt.memes.service.Interface.ISubGroup;
import com.bupt.memes.util.Preconditions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SubGroupImpl implements ISubGroup {

    final MongoTemplate template;

    @Override
    @Transactional
    public SubmissionGroup createGroup(List<String> submissionIds) {
        List<Submission> submissions = ensureSubmissionsExist(submissionIds);
        Preconditions.checkArgument(submissions.size() == submissionIds.size(), AppException.invalidParam("submissionIds"));
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
        Preconditions.checkArgument(submissionGroup != null,
                AppException.invalidParam("groupId"));

        List<Submission> submissions = ensureSubmissionsExist(submissionIds);
        Preconditions.checkArgument(submissions.size() == submissionIds.size(),
                AppException.invalidParam("submissionIds"));

        Preconditions.checkArgument(submissions.stream().noneMatch(submission -> submission.getSubmissionType() == SubmissionType.BATCH),
                AppException.invalidParam("submissionIds"));

        submissionGroup.addSubmissions(submissions);
        template.save(submissionGroup, WAITING_SUBMISSION);
        submissions.forEach(submission -> template.remove(submission, WAITING_SUBMISSION));
        return submissionGroup;
    }

    @Override
    public SubmissionGroup getById(String id) {
        Preconditions.checkArgument(id != null, AppException.invalidParam("id"));
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
        Preconditions.checkArgument(submissionsId != null, AppException.invalidParam("submissionsId"));
        return template.find(Query.query(Criteria.where("id").in(submissionsId)), Submission.class, WAITING_SUBMISSION);
    }
}

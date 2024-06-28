package com.memes.service.submission;

import com.memes.model.submission.SubmissionGroup;

import java.util.List;

public interface SubGroupService {
    SubmissionGroup createGroup(List<String> submissionIds);

    SubmissionGroup addToGroup(String groupId, List<String> submissionIds);

    SubmissionGroup getById(String id);

}

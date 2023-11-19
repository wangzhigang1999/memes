package com.bupt.memes.service.Interface;

import com.bupt.memes.model.media.SubmissionGroup;

import java.util.List;

public interface ISubGroup {
    SubmissionGroup createGroup(List<String> submissionIds);

    SubmissionGroup addToGroup(String groupId, List<String> submissionIds);


    SubmissionGroup getById(String id);

}

package com.bupt.memes.model.media;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Document(collection = "submission")
@Accessors(chain = true)
@Data
public class SubmissionGroup extends Submission {

    List<Submission> children = new ArrayList<>();

    public SubmissionGroup() {
        super();
        this.submissionType = SubmissionType.BATCH;
        this.timestamp = 0;
    }

    public void addSubmissions(List<Submission> submissions) {
        this.children.addAll(submissions);
    }


    @Override
    public void setSubmissionType(String mime) {
        this.submissionType = SubmissionType.BATCH;
    }

    @Override
    public boolean textFormat() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SubmissionGroup) {
            return this.id.equals(((SubmissionGroup) obj).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public static Optional<SubmissionGroup> fromSubmission(List<Submission> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            return Optional.empty();
        }
        SubmissionGroup submissionGroup = new SubmissionGroup();
        submissionGroup.addSubmissions(submissions);
        if (submissionGroup.children.isEmpty()) {
            return Optional.empty();
        }
        submissionGroup.id = submissionGroup.children.get(0).id;
        submissionGroup.timestamp = submissionGroup.children.get(0).timestamp;
        submissionGroup.uploader = submissionGroup.children.get(0).uploader;
        submissionGroup.hash = submissionGroup.children.get(0).hash;
        return Optional.of(submissionGroup);
    }
}

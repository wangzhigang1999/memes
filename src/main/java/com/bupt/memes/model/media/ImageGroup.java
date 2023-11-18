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
public class ImageGroup extends Submission {

    List<Submission> images = new ArrayList<>();

    public ImageGroup() {
        super();
        this.submissionType = SubmissionType.BATCH_IMAGE;
        this.timestamp = 0;
    }

    public void addSubmissions(List<Submission> submissions) {
        this.images.addAll(submissions);
    }


    @Override
    public void setSubmissionType(String mime) {
        this.submissionType = SubmissionType.BATCH_IMAGE;
    }

    @Override
    public boolean textFormat() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImageGroup) {
            return this.id.equals(((ImageGroup) obj).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public static Optional<ImageGroup> fromSubmission(List<Submission> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            return Optional.empty();
        }
        ImageGroup imageGroup = new ImageGroup();
        imageGroup.addSubmissions(submissions);
        if (imageGroup.images.isEmpty()) {
            return Optional.empty();
        }
        imageGroup.id = imageGroup.images.get(0).id;
        imageGroup.timestamp = imageGroup.images.get(0).timestamp;
        imageGroup.uploader = imageGroup.images.get(0).uploader;
        return Optional.of(imageGroup);
    }
}

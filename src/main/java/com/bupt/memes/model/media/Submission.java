package com.bupt.memes.model.media;

import com.bupt.memes.service.Interface.IndexMapKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Submission")
@AllArgsConstructor
@Accessors(chain = true)
public class Submission implements IndexMapKey {

    String id;

    SubmissionType submissionType;
    String url;
    String content;

    String uploader;
    Integer hash;
    String name;
    Boolean deleted = false;
    Boolean reviewed = false;
    long timestamp;

    long up;

    long down;

    public Submission() {
        this.timestamp = System.currentTimeMillis();
    }

    public Submission(int hashcode) {
        this.hash = hashcode;
    }

    public void setSubmissionType(String mime) {
        if (mime.startsWith("image")) {
            this.submissionType = SubmissionType.IMAGE;
        } else if (mime.startsWith("video")) {
            this.submissionType = SubmissionType.VIDEO;
        } else if (mime.startsWith("text/bilibili")) {
            this.submissionType = SubmissionType.BILIBILI;
        } else if (mime.startsWith("text/markdown")) {
            this.submissionType = SubmissionType.MARKDOWN;
        }
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Submission) {
            return ((Submission) obj).getHash().equals(this.hash);
        }
        return false;
    }

    @Override
    public String getIndexMapKey() {
        return id;
    }
}

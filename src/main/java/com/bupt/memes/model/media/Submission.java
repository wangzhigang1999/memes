package com.bupt.memes.model.media;

import com.bupt.memes.service.Interface.IndexMapKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@Document(collection = "Submission")
@AllArgsConstructor
@Accessors(chain = true)
public class Submission implements IndexMapKey, Comparable<Submission> {

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

    public Submission(String id) {
        this.id = id;
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
        if (hash == null) {
            hash = Objects.hash(id);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        // if not instance of Submission, return false
        if (!(obj instanceof Submission)) {
            return false;
        }

        // then check if is not null and equal
        if (obj == this || Objects.equals(((Submission) obj).id, this.id)) {
            return true;
        }

        return ((Submission) obj).getHash().equals(this.hash);
    }

    @Override
    public String getIndexMapKey() {
        return id;
    }


    @Override
    public int compareTo(Submission o) {
        return this.id.compareTo(o.id);
    }
}

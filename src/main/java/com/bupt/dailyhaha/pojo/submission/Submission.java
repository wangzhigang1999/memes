package com.bupt.dailyhaha.pojo.submission;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Submission")
@AllArgsConstructor
public class Submission {

    SubmissionType submissionType;
    String url;
    Integer hash;
    String name;
    Boolean deleted = false;
    long timestamp;

    long up;

    long down;

    public Submission() {
        this.timestamp = System.currentTimeMillis();
    }

    public void setSubmissionType(String mime) {
        if (mime.startsWith("image")) {
            this.submissionType = SubmissionType.IMAGE;
        } else if (mime.startsWith("video")) {
            this.submissionType = SubmissionType.VIDEO;
        } else if (mime.startsWith("text/bilibili")) {
            this.submissionType = SubmissionType.BILIBILI;
        }
    }
}

package com.bupt.dailyhaha.pojo.submission;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "Video")
public class Video extends Submission {
    public Video() {
        this.submissionType = SubmissionType.VIDEO;
    }

    public static void main(String[] args) {
        Submission submission = new Video();
        System.out.println(submission.submissionType);
    }

}

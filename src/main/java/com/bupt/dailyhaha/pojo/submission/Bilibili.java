package com.bupt.dailyhaha.pojo.submission;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "Bilibili")
@Data
public class Bilibili extends Submission {
    public Bilibili() {
        this.submissionType = SubmissionType.BILIBILI;
    }
}

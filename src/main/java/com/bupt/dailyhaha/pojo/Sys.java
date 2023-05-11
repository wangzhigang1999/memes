package com.bupt.dailyhaha.pojo;

import com.bupt.dailyhaha.pojo.media.Submission;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("sys")
@Data
public class Sys {
    private String id = "sys";
    Boolean botUp = true;

    int MAX_SUBMISSIONS = 50;

    Set<String> releaseStrategy = Set.of();
    String selectedReleaseStrategy = "default";

    Set<Submission> topSubmission = Set.of();
}

package com.bupt.dailyhaha.pojo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("sys")
@Data
public class Sys {
    private String id = "sys";
    Boolean botUp = true;

    Set<Submission> topSubmission = Set.of();
}

package com.bupt.memes.pojo.media;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("history")
@Data
public class History {
    long timestamp;
    String date;
    int count;
    List<Submission> submissions;

}

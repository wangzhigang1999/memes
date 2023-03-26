package com.bupt.dailyhaha.pojo.submission;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("history")
@Data
public class History {
    long timestamp;
    String date;
    int count;
    List<Image> images;
    List<Video> videos;
    List<Bilibili> bilibilis;

    List<Submission> submissions;

}

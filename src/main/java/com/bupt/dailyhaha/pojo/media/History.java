package com.bupt.dailyhaha.pojo.media;

import lombok.Data;

import java.util.List;

@Data
public class History {
    long timestamp;
    String date;
    int count;
    List<Submission> submissions;

}

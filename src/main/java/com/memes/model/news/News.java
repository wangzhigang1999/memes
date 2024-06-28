package com.memes.model.news;

import com.google.gson.Gson;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "news")
@Data
public class News {
    private String id;
    private long timestamp;

    private String date;

    private String coverImage;
    private String title;

    private String introduction;
    private String content;
    private String sourceURL;
    private String author;
    private Set<String> tag;
    private int like;
    private int dislike;
    private boolean deleted;

    public boolean validate() {
        return title != null && content != null && sourceURL != null;
    }

    public static News fromJson(String data) {
        return new Gson().fromJson(data, News.class);
    }
}

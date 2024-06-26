package com.memes.model.rss;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "rss_item")
public class RSSItem {
    private String id;
    private String title;
    private String link;
    private String author;
    @JsonIgnore
    private String description;
    private String pubDate;
    private String comments;
    private String guid;

    private String board = "";
}

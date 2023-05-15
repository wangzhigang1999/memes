package com.bupt.dailyhaha.pojo.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@org.springframework.data.mongodb.core.mapping.Document("document")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Document {
    String id;

    String coverImage;

    String briefContent;
    String title;
    String content;
    String author;
    long createTime;
    long updateTime;
    int like;
    int dislike;

    String rawContent;

    DocType type = DocType.Markdown;

    boolean deleted = false;

    boolean privateDoc = false;

}

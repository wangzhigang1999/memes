package com.bupt.dailyhaha.pojo.doc;

import com.google.gson.Gson;
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

    String cover_image;

    String brief_content;
    String title;
    String content;
    String author;
    long createTime;
    long updateTime;
    int like;
    int dislike;

    String raw_content;

    DocType type = DocType.Markdown;

    public static void main(String[] args) {
        Gson gson = new Gson();
        Document document = new Document();
        document.setType(DocType.ByrHtml);
        System.out.print( gson.toJson(document));
    }

}

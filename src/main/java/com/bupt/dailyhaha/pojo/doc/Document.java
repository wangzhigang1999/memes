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

    public static void main(String[] args) {
        Document document = new Document();
        document.setCover_image("https://img.52z.com/upload/news/image/20180508/20180508081621_31037.jpg");
        document.setBrief_content("这是一篇测试文章");
        document.setTitle("测试文章");
        document.setContent("这是一篇测试文章");
        document.setAuthor("测试作者");
        document.setCreateTime(1621094400000L);
        document.setUpdateTime(1621094400000L);
        document.setLike(0);
        document.setDislike(0);
        System.out.println(new Gson().toJson(document));

    }

}

package com.bupt.dailyhaha.service.Interface;

import com.bupt.dailyhaha.pojo.media.News;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class INewsTest {

    @Autowired
    private INews iNews;

//    @Test
//    void addNews() {
//
//        for (int i = 0; i < 100; i++) {
//            News news = News.randomNews();
//            iNews.addNews(news, null);
//        }
//    }

//    @Test
//    @Order(1)
//    void addTag() {
//        News news = iNews.addTag("64a6b3cc774d314c24d0d42c", Set.of("99"));
//        assert news.getTag().contains("99");
//    }
//
//    @Test
//    @Order(3)
//    void removeTag() {
//        News news = iNews.removeTag("64a6b3cc774d314c24d0d42c", Set.of("99"));
//        assert !news.getTag().contains("99");
//    }
//
//    @Test
//    void findById() {
//    }
//
//    @Test
//    void deleteNews() {
//    }
//
//    @Test
//    void find() {
//    }
//
//    @Test
//    @Order(2)
//    void findByTag() {
//        iNews.findByTag(Set.of("99"), 1, 10, null).getList().forEach(System.out::println);
//    }

    @Test
    void findByDate() {
        long start = System.currentTimeMillis();
        List<News> byMMDD = iNews.findByMMDD("05-01");
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println(byMMDD.size());

    }
}
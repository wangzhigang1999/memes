package com.memes.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.memes.mapper.MediaMapper;
import com.memes.model.pojo.MediaContent;
import com.memes.util.RandomMediaContentGenerator;
import com.memes.util.TimeUtil;

import lombok.SneakyThrows;

@SpringBootTest
class MediaContentServiceTest {

    @Autowired
    MediaMapper mapper;

    @Autowired
    MediaContentService service;

    private static final int N = 10;
    List<Integer> ids = new ArrayList<>();

    @SneakyThrows
    @BeforeEach
    void setUp() {
        List<MediaContent> mediaContents = RandomMediaContentGenerator.generateRandomMediaContents(N);
        for (MediaContent mediaContent : mediaContents) {
            service.save(mediaContent);
            ids.add(mediaContent.getId());
        }
    }

    @AfterEach
    void tearDown() {
        for (Integer id : ids) {
            service.removeById(id);
        }
        ids.clear();
    }

    @Test
    void listPendingMediaContent() {
        List<MediaContent> mediaContents = service.listPendingMediaContent(10);
        assert !mediaContents.isEmpty();
    }

    @Test
    void markMediaStatus() {
        for (Integer id : ids) {
            service.markMediaStatus(id, MediaContent.ContentStatus.APPROVED);
        }

        for (Integer id : ids) {
            MediaContent mediaContent = mapper.selectById(id);
            assert mediaContent.getStatus() == MediaContent.ContentStatus.APPROVED;
        }
    }

    @Test
    void batchMarkMediaStatus() {
        assert service.batchMarkMediaStatus(ids, MediaContent.ContentStatus.APPROVED) == ids.size();
    }

    @Test
    void getNumByStatusAndDate() {
        service.batchMarkMediaStatus(ids, MediaContent.ContentStatus.APPROVED);
        assert service.getNumByStatusAndDate(MediaContent.ContentStatus.APPROVED, TimeUtil.getYMD()) == ids.size();
    }
}

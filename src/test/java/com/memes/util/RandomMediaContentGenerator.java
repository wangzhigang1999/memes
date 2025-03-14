package com.memes.util;

import java.time.LocalDateTime;
import java.util.*;

import com.memes.model.pojo.MediaContent;
import com.memes.model.pojo.MediaContent.AiModerationStatus;
import com.memes.model.pojo.MediaContent.ContentStatus;
import com.memes.model.pojo.MediaContent.DataType;

public class RandomMediaContentGenerator {

    private static final Random random = new Random();
    private static final String[] userIds = {"user123", "user456", "user789"};
    private static final String[] checksums = {"abc123", "def456", "ghi789"};
    private static final String[] llmDescriptions = {"A beautiful landscape photo.", "A short comedy video.", "A simple markdown text."};
    private static final String[] rejectionReasons = {null, "Contains inappropriate language.", "Does not meet guidelines."};
    private static final String[][] tags = {{"nature", "landscape"}, {"comedy", "short"}, {"markdown", "text"}};
    private static final long[] fileSizes = {2048000, 15728640, 1024};
    private static final String[] dataContents = {
        "https://example.com/image1.jpg",
        "https://example.com/video1.mp4",
        "# Hello World\nThis is a markdown content."};

    public static List<MediaContent> generateRandomMediaContents(int n) {
        List<MediaContent> mediaContents = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            mediaContents.add(generateRandomMediaContent());
        }
        return mediaContents;
    }

    private static MediaContent generateRandomMediaContent() {
        return MediaContent
            .builder()
            .id(random.nextInt())
            .dataType(DataType.values()[random.nextInt(DataType.values().length)])
            .dataContent(dataContents[random.nextInt(dataContents.length)])
            .userId(userIds[random.nextInt(userIds.length)])
            .checksum(checksums[random.nextInt(checksums.length)])
            .llmDescription(llmDescriptions[random.nextInt(llmDescriptions.length)])
            .llmModerationStatus(AiModerationStatus.values()[random.nextInt(AiModerationStatus.values().length)])
            .rejectionReason(rejectionReasons[random.nextInt(rejectionReasons.length)])
            .tags(List.of(tags[random.nextInt(tags.length)]))
            .fileSize(fileSizes[random.nextInt(fileSizes.length)])
            .metadata(generateRandomMetadata())
            .status(ContentStatus.values()[random.nextInt(ContentStatus.values().length)])
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private static Map<String, Object> generateRandomMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", random.nextInt(100));
        metadata.put("key2", "value%d".formatted(random.nextInt(10)));
        return metadata;
    }

    public static void main(String[] args) {
        int n = 5; // 生成5个随机对象
        List<MediaContent> mediaContents = generateRandomMediaContents(n);
        mediaContents.forEach(System.out::println);
    }
}

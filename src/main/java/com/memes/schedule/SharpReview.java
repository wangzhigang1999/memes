package com.memes.schedule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.memes.model.pojo.MediaContent;
import com.memes.service.MediaContentService;
import com.memes.util.GsonUtil;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
public class SharpReview {

    final MeterRegistry registry;

    private static final String MODEL = "deepseek-v3";

    private static final String SYSTEM = Role.SYSTEM.getValue();

    private static Message SYS_MSG;

    @Value("classpath:sharp_review.xml")
    private Resource promptResource;

    @PostConstruct
    public void init() throws IOException {
        String SYS_PROMPT = StreamUtils.copyToString(promptResource.getInputStream(), StandardCharsets.UTF_8);
        SYS_MSG = Message
            .builder()
            .role(SYSTEM)
            .content(SYS_PROMPT)
            .build();
        startReview();
    }

    @Value("${dashscope.apiKey}")
    private String apiKey;

    private final MediaContentService mediaContentService;
    private final ExecutorService reviewExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setName("sharp-review-thread");
        return thread;
    });

    public SharpReview(MeterRegistry registry, MediaContentService mediaContentService) {
        this.registry = registry;
        this.mediaContentService = mediaContentService;
    }

    private void startReview() {
        reviewExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    List<MediaContent> mediaContents = mediaContentService.listNoSharpReviewMediaContent(100);
                    mediaContents
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(mediaContent -> StringUtils.isNotEmpty(mediaContent.getLlmDescription()))
                        .forEach(this::sharpReview);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error in sharp review process", e);
                }
            }
        });
    }

    /**
     * 图片锐评
     */
    @SneakyThrows
    private void sharpReview(MediaContent mediaContent) {
        log.info("开始锐评：{}", mediaContent.getId());
        try {
            Generation gen = new Generation();
            Message userMsg = Message
                .builder()
                .role(Role.USER.getValue())
                .content(mediaContent.getLlmDescription())
                .build();
            GenerationParam param = GenerationParam
                .builder()
                .apiKey(apiKey)
                .model(MODEL)
                .messages(Arrays.asList(SYS_MSG, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
            GenerationResult call = gen.call(param);
            GenerationUsage usage = call.getUsage();
            log.info("LLM API Usage: {}", GsonUtil.toJson(usage));
            String content = call.getOutput().getChoices().getFirst().getMessage().getContent();
            if (StringUtils.isNotEmpty(content)) {
                log.info("LLM Output: {}", content);
                mediaContent.setSharpReview(content);
            } else {
                log.warn("LLM Output is empty for media content: {}", mediaContent.getId());
                mediaContent.setSharpReview("[REVIEW_FAILED]");
            }
            mediaContentService.updateById(mediaContent);
            registry.counter("total_token", "model", MODEL).increment(usage.getTotalTokens());
            registry.counter("input_token", "model", MODEL).increment(usage.getInputTokens());
            registry.counter("output_token", "model", MODEL).increment(usage.getOutputTokens());
        } catch (Exception e) {
            log.error("Review failed for media content: {}", mediaContent.getId(), e);
            mediaContent.setSharpReview("[REVIEW_FAILED]");
            mediaContentService.updateById(mediaContent);
        }
    }
}

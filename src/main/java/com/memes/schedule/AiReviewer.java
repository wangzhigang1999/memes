package com.memes.schedule;

import static com.memes.util.GsonUtil.extractJsonFromModelOutput;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationUsage;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.common.Status;
import com.alibaba.dashscope.exception.ApiException;
import com.google.protobuf.util.JsonFormat;
import com.memes.model.pojo.MediaContent;
import com.memes.model.transport.LLMReviewResult;
import com.memes.model.transport.ReviewOutcome;
import com.memes.service.MediaContentService;
import com.memes.util.GsonUtil;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
public class AiReviewer {

    final MeterRegistry registry;

    private static final String MODEL = "qwen-vl-max-latest";

    private static final String SYSTEM = Role.SYSTEM.getValue();

    private static final String USER = Role.USER.getValue();

    private static final String TYPE_TEXT = "text";

    private static final String TYPE_IMAGE = "image";

    private static final String REVIEW_PROMPT = "请审核这个图片";

    private static String SYS_PROMPT;

    @Value("classpath:prompt.xml")
    private Resource promptResource;

    @PostConstruct
    public void init() throws IOException {
        SYS_PROMPT = StreamUtils.copyToString(promptResource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Value("${dashscope.apiKey}")
    private String apiKey;

    private final MediaContentService mediaContentService;

    private final MultiModalConversation conv = new MultiModalConversation();

    public AiReviewer(MeterRegistry registry, MediaContentService mediaContentService) {
        this.registry = registry;
        this.mediaContentService = mediaContentService;
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        List<MediaContent> mediaContents = mediaContentService.listPendingMediaContent(100);
        mediaContents
            .stream()
            .filter(Objects::nonNull)
            .filter(mediaContent -> mediaContent.getDataType() == MediaContent.DataType.IMAGE)
            .forEach(this::processMediaContentReview);
    }

    /**
     * 调用 LLM API
     *
     * @param url
     *            图片链接
     * @return LLMReviewResult
     */
    public LLMReviewResult callWithRemoteImage(String url) {
        try {
            log.debug("Calling LLM API with URL: {}", url);

            // 直接内联创建消息，移除单独的方法
            List<MultiModalMessage> messages = Arrays
                .asList(
                    // 系统消息
                    MultiModalMessage.builder().role(SYSTEM).content(List.of(Collections.singletonMap(TYPE_TEXT, SYS_PROMPT))).build(),
                    // 用户消息
                    MultiModalMessage
                        .builder()
                        .role(USER)
                        .content(Arrays.asList(Collections.singletonMap(TYPE_IMAGE, url), Collections.singletonMap(TYPE_TEXT, REVIEW_PROMPT)))
                        .build());

            // 创建参数
            MultiModalConversationParam param = MultiModalConversationParam.builder().apiKey(apiKey).model(MODEL).messages(messages).build();

            // 调用 API
            MultiModalConversationResult result = conv.call(param);

            // 处理使用量统计
            MultiModalConversationUsage usage = result.getUsage();
            log.info("LLM API Usage: {}", GsonUtil.toJson(usage));
            registry.counter("total_token", "model", MODEL).increment(usage.getTotalTokens());
            registry.counter("input_token", "model", MODEL).increment(usage.getInputTokens());
            registry.counter("image_token", "model", MODEL).increment(usage.getImageTokens());
            registry.counter("output_token", "model", MODEL).increment(usage.getOutputTokens());
            log.info("Sent LLM Usage Data to message queue.");

            // 提取并解析模型输出
            String modelOut = result.getOutput().getChoices().getFirst().getMessage().getContent().getFirst().get(TYPE_TEXT).toString();

            String jsonStr = extractJsonFromModelOutput(modelOut);
            log.debug("Raw LLM Output: {}", jsonStr);

            LLMReviewResult.Builder builder = LLMReviewResult.newBuilder();
            JsonFormat.parser().merge(jsonStr, builder);
            return builder.build();

        } catch (ApiException e) {
            Status status = e.getStatus();
            if (status.getStatusCode() == 400) {
                registry.counter("llm_inappropriate_content", "model", MODEL).increment();
                return LLMReviewResult.newBuilder().setOutcome(ReviewOutcome.FLAGGED).setFailureReason(e.getMessage()).build();
            }
            throw e;
        } catch (Exception e) {
            log.error("Error calling LLM API. URL: {}", url, e);
            registry.counter("llm_api_error", "model", MODEL).increment();
            return LLMReviewResult.newBuilder().setOutcome(ReviewOutcome.FLAGGED).setFailureReason("LLM API call failed").build();
        } finally {
            log.debug("LLM API call completed.");
        }
    }

    /**
     * 处理媒体内容审核
     */
    private void processMediaContentReview(MediaContent mediaContent) {
        log.info("开始处理媒体内容：{}", mediaContent.getId());

        if (mediaContent.getDataType() != MediaContent.DataType.IMAGE) {
            log.warn("不支持的媒体类型：{}，跳过审核", mediaContent.getDataType());
            return;
        }

        // 执行图片审核
        log.info("正在审核图片内容：{}", mediaContent.getId());
        LLMReviewResult result = callWithRemoteImage(mediaContent.getDataContent());
        // 记录指标
        registry.counter("llm_review_count", "outcome", result.getOutcome().name()).increment();
        log.info("AI 审核结果：{} - 媒体 ID: {}", result.getOutcome().name(), mediaContent.getId());

        // 更新媒体内容
        mediaContent.setLlmDescription(result.getMediaDescription());
        mediaContent.setRejectionReason(result.getFailureReason());
        mediaContent.setLlmModerationStatus(MediaContent.AiModerationStatus.valueOf(result.getOutcome().name()));
        mediaContentService.updateById(mediaContent);

        // 处理审核结果
        ReviewOutcome outcome = result.getOutcome();
        if (outcome == ReviewOutcome.APPROVED) {
            log.info("媒体内容 {} 通过审核，正在进行批准", mediaContent.getId());
            boolean updateSuccess = mediaContentService.markMediaStatus(mediaContent.getId(), MediaContent.ContentStatus.APPROVED);
            log.info(updateSuccess ? "媒体内容 {} 已成功批准" : "媒体内容 {} 批准失败", mediaContent.getId());
        } else if (outcome == ReviewOutcome.FLAGGED || outcome == ReviewOutcome.REJECTED) {
            log.info("媒体内容 {} 被标记或拒绝，将进行人工审核", mediaContent.getId());
        } else {
            log.error("媒体内容 {} 出现未知审核结果，将进行人工审核", mediaContent.getId());
        }
    }
}

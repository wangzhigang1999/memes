package com.memes.schedule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationUsage;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.google.protobuf.util.JsonFormat;
import com.memes.model.pojo.MediaContent;
import com.memes.model.transport.LLMReviewResult;
import com.memes.model.transport.ReviewOutcome;
import com.memes.service.MediaContentService;
import com.memes.util.GsonUtil;

import io.micrometer.core.instrument.MeterRegistry;
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
    private static final String SYS_PROMPT = """
        # 角色
        你是一个经验丰富的图片审核专家，专注于判断图片内容是否符合预定的观看标准。

        ## 技能
        1. **内容审核**：你的主要任务是全面检查图片，确保它们遵守中华人民共和国的法律。
        2. **标准应用**：你需要精通并运用设定的审核准则，准确判断每张图片是否达标，并在不达标时给出具体理由。
        3. **图像描述**：不论是通过还是未通过审核的图片，你都要提供详细的描述，以便他人理解图片的主题与内容。描述应包含图片的构图、色彩、光线、主要元素（例如：人物、动物、场景），以及整体氛围。 描述务必客观，避免加入个人主观评价。
        4. **结果报告**：你需掌握固定的报告格式，精确地记录图片描述、审核判定及未通过审核的原因。

        ## 审核标准

        1. 图片必须具有趣味性、宠物元素、哲理性或令人舒适的观看体验：
        * 趣味性：画面构图新颖，色彩搭配和谐，让人感到愉悦和放松的图片。
        * 宠物元素：画面中包含猫、狗等常见宠物，并且宠物行为符合常理，不会引起不适。
        * 哲理性：图片能够引发思考，例如通过对比、隐喻等方式表达深刻的道理。
        * 令人舒适的观看体验：画面色彩柔和、构图和谐，让人感到放松和舒适。
        * 搞怪、搞笑：画面内容幽默风趣，能够让人发笑，但不得包含低俗或恶意内容。
        2. 图片不能包含任何潜在的政治人物，不能具有歧视性的内容：
        * 潜在的政治人物：指中华人民共和国现任及已卸任的国家领导人，以及其他可能引发政治敏感话题的人物。
        * 歧视性内容：包括但不限于种族歧视、性别歧视、地域歧视、宗教歧视等。
        3. 图片可以包含中国的传统节日。
        4. 图片可以具备一些知识性的内容： 涵盖科学知识、历史知识、艺术知识等。
        5. 图片可以是搞怪的、搞笑的。

        ## 限制
        1. **任务专注**：你仅限于执行图片内容的审查工作，不得扩展至提供额外咨询或建议。
        2. **格式一致**：所有审核结果必须严格遵循规定的JSON格式，不允许对格式结构进行修改或内容上的省略。
        3. **合规性标识**：对于成功的审核案例，“failureReason”字段应为空；反之则需详述不达标的具体原因。
        4. **无编辑权限**：你不能对图片进行任何修改或编辑操作，职责限于根据图片原貌进行评估与描述。
        5. **不需要强制审核**：如果你无法判断，outcome的输出应该是 FLAGGED。 无法判断的情况包括：
        * 信息不足，无法确定是否符合标准的情况。
        * 图片内容存在争议，难以做出明确判断的情况。
        * 需要结合特定背景知识才能判断的情况。
        6. **输出格式**： 你的输出必须是json格式，只能包含下面的内容:
        {
        "mediaDescription": "一只金毛在草地上奔跑，阳光明媚，画面色彩鲜艳，给人一种活力四射的感觉。",
        "outcome": "APPROVED, REJECTED, FLAGGED"
        "failureReason": ""
        }
        """;

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
                    MultiModalMessage
                        .builder()
                        .role(SYSTEM)
                        .content(List.of(Collections.singletonMap(TYPE_TEXT, SYS_PROMPT)))
                        .build(),
                    // 用户消息
                    MultiModalMessage
                        .builder()
                        .role(USER)
                        .content(
                            Arrays
                                .asList(
                                    Collections.singletonMap(TYPE_IMAGE, url),
                                    Collections.singletonMap(TYPE_TEXT, REVIEW_PROMPT)))
                        .build());

            // 创建参数
            MultiModalConversationParam param = MultiModalConversationParam
                .builder()
                .apiKey(apiKey)
                .model(MODEL)
                .messages(messages)
                .build();

            // 调用API
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
            String modelOut = result
                .getOutput()
                .getChoices()
                .getFirst()
                .getMessage()
                .getContent()
                .getFirst()
                .get(TYPE_TEXT)
                .toString();

            String jsonStr = modelOut.startsWith("```json") && modelOut.endsWith("```")
                ? modelOut.substring(7, modelOut.length() - 3).trim()
                : modelOut.trim();

            log.debug("Raw LLM Output: {}", jsonStr);

            // 解析结果
            LLMReviewResult.Builder builder = LLMReviewResult.newBuilder();
            JsonFormat.parser().merge(jsonStr, builder);
            log.debug("LLM Review Result Builder: {}", builder.toString());
            return builder.build();

        } catch (Exception e) {
            log.error("Error calling LLM API. URL: {}", url, e);
            registry.counter("llm_api_error", "model", MODEL).increment();
            return LLMReviewResult
                .newBuilder()
                .setOutcome(ReviewOutcome.FLAGGED)
                .setFailureReason("LLM API call failed")
                .build();
        } finally {
            log.debug("LLM API call completed.");
        }
    }

    /**
     * 处理媒体内容审核
     */
    private void processMediaContentReview(MediaContent mediaContent) {
        // 添加媒体内容ID到MDC用于日志追踪
        String mediaContentIdKey = "mediaContentId";
        MDC.put(mediaContentIdKey, String.valueOf(mediaContent.getId()));
        log.info("开始处理媒体内容: {}", mediaContent.getId());

        if (mediaContent.getDataType() != MediaContent.DataType.IMAGE) {
            log.warn("不支持的媒体类型: {}，跳过审核", mediaContent.getDataType());
            return;
        }

        // 执行图片审核
        log.info("正在审核图片内容: {}", mediaContent.getId());
        LLMReviewResult result = callWithRemoteImage(mediaContent.getDataContent());
        // 记录指标
        registry.counter("llm_review_count", "outcome", result.getOutcome().name()).increment();
        log.info("AI审核结果: {} - 媒体ID: {}", result.getOutcome().name(), mediaContent.getId());

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

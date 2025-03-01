package com.memes.schedule;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationUsage;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.google.gson.Gson;
import com.google.protobuf.util.JsonFormat;
import com.memes.model.submission.Submission;
import com.memes.model.submission.SubmissionType;
import com.memes.model.transport.LLMReviewResult;
import com.memes.model.transport.MediaType;
import com.memes.model.transport.ReviewOutcome;
import com.memes.service.MessageQueueService;
import com.memes.service.review.ReviewService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
public class Reviewer {

    final MeterRegistry registry;

    private static final String MODEL = "qwen-vl-max-latest";
    private static final String MEMES_QUEUE = "memes";
    private static final String SYSTEM = Role.SYSTEM.getValue();
    private static final String USER = Role.USER.getValue();
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_IMAGE = "image";
    private static final String REVIEW_PROMPT = "请审核这个图片";
    private static final String ACCEPT_ACTION = "accept";
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
                *   趣味性：画面构图新颖，色彩搭配和谐，让人感到愉悦和放松的图片。
                *   宠物元素：画面中包含猫、狗等常见宠物，并且宠物行为符合常理，不会引起不适。
                *   哲理性：图片能够引发思考，例如通过对比、隐喻等方式表达深刻的道理。
                *   令人舒适的观看体验：画面色彩柔和、构图和谐，让人感到放松和舒适。
                *   搞怪、搞笑：画面内容幽默风趣，能够让人发笑，但不得包含低俗或恶意内容。
            2. 图片不能包含任何潜在的政治人物，不能具有歧视性的内容：
                *   潜在的政治人物：指中华人民共和国现任及已卸任的国家领导人，以及其他可能引发政治敏感话题的人物。
                *   歧视性内容：包括但不限于种族歧视、性别歧视、地域歧视、宗教歧视等。
            3. 图片可以包含中国的传统节日。
            4. 图片可以具备一些知识性的内容： 涵盖科学知识、历史知识、艺术知识等。
            5. 图片可以是搞怪的、搞笑的。
            
            ## 限制
            1. **任务专注**：你仅限于执行图片内容的审查工作，不得扩展至提供额外咨询或建议。
            2. **格式一致**：所有审核结果必须严格遵循规定的JSON格式，不允许对格式结构进行修改或内容上的省略。
            3. **合规性标识**：对于成功的审核案例，“failureReason”字段应为空；反之则需详述不达标的具体原因。
            4. **无编辑权限**：你不能对图片进行任何修改或编辑操作，职责限于根据图片原貌进行评估与描述。
            5. **不需要强制审核**：如果你无法判断，outcome的输出应该是UNDECIDED。 无法判断的情况包括：
                * 信息不足，无法确定是否符合标准的情况。
                * 图片内容存在争议，难以做出明确判断的情况。
                * 需要结合特定背景知识才能判断的情况。
            6. **输出格式**： 你的输出必须是json格式，只能包含下面的内容 ：{
                                                                        "mediaDescription": "一只金毛在草地上奔跑，阳光明媚，画面色彩鲜艳，给人一种活力四射的感觉。",
                                                                        "outcome": "PASS/FAIL/UNDECIDED",
                                                                        "failureReason": ""
                                                                      }
            """;

    @Value("${dashscope.apiKey}")
    private String apiKey;

    private final MessageQueueService mqService;
    private final ReviewService reviewService;
    private final Gson gson = new Gson();
    private final MultiModalConversation conv = new MultiModalConversation();
    private final RedisTemplate<String, Object> redisTemplate;

    public Reviewer(MeterRegistry registry, MessageQueueService mqService, ReviewService reviewService, RedisTemplate<String, Object> redisTemplate) {
        this.registry = registry;
        this.mqService = mqService;
        this.reviewService = reviewService;
        this.redisTemplate = redisTemplate;
    }

    private MultiModalMessage createSystemMessage() {
        return MultiModalMessage.builder()
                .role(SYSTEM)
                .content(List.of(Collections.singletonMap(TYPE_TEXT, SYS_PROMPT)))
                .build();
    }

    private MultiModalMessage createUserMessage(String url) {
        return MultiModalMessage.builder()
                .role(USER)
                .content(Arrays.asList(Collections.singletonMap(TYPE_IMAGE, url), Collections.singletonMap(TYPE_TEXT, REVIEW_PROMPT)))
                .build();
    }

    public LLMReviewResult callWithRemoteImage(String url) {
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(apiKey)
                .model(MODEL)
                .messages(Arrays.asList(createSystemMessage(), createUserMessage(url)))
                .build();

        log.debug("Calling LLM API with URL: {}", url); // Debug level: URL might be sensitive.
        try {
            MultiModalConversationResult result = conv.call(param);
            MultiModalConversationUsage usage = result.getUsage();
            log.info("LLM API Usage: {}", gson.toJson(usage)); // Use JSON for readability
            registry.counter("total_token", "model", MODEL).increment(usage.getTotalTokens());
            registry.counter("input_token", "model", MODEL).increment(usage.getInputTokens());
            registry.counter("image_token", "model", MODEL).increment(usage.getImageTokens());
            registry.counter("output_token", "model", MODEL).increment(usage.getOutputTokens());
            mqService.sendMessage("usage", usage);
            log.info("Sent LLM Usage Data to message queue.");

            // Extract model output
            String modelOut = result.getOutput().getChoices().getFirst().getMessage().getContent().getFirst().get(TYPE_TEXT).toString();
            String str;
            if (modelOut.startsWith("```json") && modelOut.endsWith("```")) {
                str = modelOut.substring(7, modelOut.length() - 3).trim();
            } else {
                str = modelOut.trim();
            }
            log.debug("Raw LLM Output: {}", str); // Debug level: Raw output is good for troubleshooting
            LLMReviewResult.Builder builder = LLMReviewResult.newBuilder();
            JsonFormat.parser().merge(str, builder);
            log.debug("LLM Review Result Builder: {}", builder.toString());
            return builder.build();

        } catch (Exception e) {
            log.error("Error calling LLM API.  URL: {}", url, e); // Include URL and the exception
            registry.counter("llm_api_error", "model", MODEL).increment();
            return LLMReviewResult.newBuilder().setOutcome(ReviewOutcome.UNDECIDED).setFailureReason("LLM API call failed").build();
        } finally {
            log.debug("LLM API call completed.");
        }
    }

    @Scheduled(fixedRate = 1000)
    public void run() {
        mqService.receiveMessage(MEMES_QUEUE, Submission.class).ifPresent(this::processSubmission);
    }

    private void processSubmission(Submission submission) {
        // Add submission ID to MDC for all logs in this method
        MDC.put("submissionId", submission.getId());
        log.info("Processing submission: {}", submission.getId());

        try {
            if (submission.getSubmissionType() == SubmissionType.IMAGE) {
                LLMReviewResult reviewResult = reviewImageSubmission(submission);
                redisTemplate.opsForHash().put("submission", submission.getId(), submission);
                redisTemplate.opsForHash().put("review_result", submission.getId(), reviewResult);
                handleReviewOutcome(submission.getId(), reviewResult.getOutcome());
            } else {
                log.warn("Unsupported submission type: {}.  Skipping review.", submission.getSubmissionType());
            }
        } catch (Exception e) {
            log.error("Error processing submission: {}", submission.getId(), e); // Include the exception!
        } finally {
            MDC.remove("submissionId"); // Clean up MDC
        }
    }

    private LLMReviewResult reviewImageSubmission(Submission submission) {
        log.info("Reviewing image submission: {}", submission.getId());
        LLMReviewResult result = callWithRemoteImage(submission.getUrl());
        registry.counter("llm_review_count", "outcome", result.getOutcome().name()).increment();
        log.info("LLM review outcome: {} for submission: {}", result.getOutcome().name(), submission.getId());
        return result.toBuilder()
                .setMediaId(submission.getId())
                .setInputPrompt(REVIEW_PROMPT)
                .setMediaType(MediaType.valueOf(submission.getSubmissionType().name()))
                .setReviewerModel(MODEL)
                .setReviewTimestamp(System.currentTimeMillis())
                .build();
    }

    private void handleReviewOutcome(String submissionId, ReviewOutcome outcome) {
        switch (outcome) {
            case PASS:
                log.info("Submission {} passed review.  Approving.", submissionId);
                reviewService.reviewSubmission(submissionId, ACCEPT_ACTION);
                break;
            case UNDECIDED:
            case FAIL:
                log.info("Submission {} is undecided or failed, will be reviewed manually", submissionId);
                break;
            default:
                log.error("Submission {} has an unknown outcome, will be reviewed manually", submissionId);
        }
    }
}
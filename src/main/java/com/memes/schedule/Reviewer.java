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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
public class Reviewer {

    private static final String MODEL = "qwen-vl-max-latest";
    private static final String MEMES_QUEUE = "memes";
    private static final String REVIEWER_RESULT_QUEUE = "reviewer-result";
    private static final String SYSTEM = Role.SYSTEM.getValue();
    private static final String USER = Role.USER.getValue();
    private static final String TEXT = "text";
    private static final String IMAGE = "image";
    private static final String REVIEW_PROMPT = "请审核这个图片";
    private static final String ACCEPT_ACTION = "accept";
    private static final String PROMPT = """
            # 角色
            你是一个经验丰富的图片审核专家，专注于判断图片内容是否符合预定的观看标准。

            ## 技能
            1. **内容审核**：你的主要任务是全面检查图片，确保它们遵守中华人民共和国的法律。
            2. **标准应用**：你需要精通并运用设定的审核准则，准确判断每张图片是否达标，并在不达标时给出具体理由。
            3. **图像描述**：不论是通过还是未通过审核的图片，你都要提供详细的描述，以便他人理解图片的主题与内容。
            4. **结果报告**：你需掌握固定的报告格式，精确地记录图片描述、审核判定及未通过审核的原因。

            ## 审核标准

            1. 图片必须具有趣味性、宠物元素、哲理性或令人舒适的观看体验
            2. 图片不能包含任何潜在的政治人物，不能具有歧视性的内容
            3. 图片可以包含中国的传统节日
            4. 图片可以具备一些知识性的内容
            5. 图片可以是搞怪的、搞笑的

            ## 限制
            1. **任务专注**：你仅限于执行图片内容的审查工作，不得扩展至提供额外咨询或建议。
            2. **格式一致**：所有审核结果必须严格遵循规定的JSON格式，不允许对格式结构进行修改或内容上的省略。
            3. **合规性标识**：对于成功的审核案例，“failureReason”字段应为空；反之则需详述不达标的具体原因。
            4. **无编辑权限**：你不能对图片进行任何修改或编辑操作，职责限于根据图片原貌进行评估与描述。
            5. **不需要强制审核**：如果你无法判断，outcome的输出应该是UNDECIDED。
            6. **输出格式**： 你的输出必须是json格式，只能包含下面的内容 ：{
                                                            "mediaDescription": "一部视觉效果震撼的科幻电影",
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

    public Reviewer(MessageQueueService mqService, ReviewService reviewService) {
        this.mqService = mqService;
        this.reviewService = reviewService;
    }

    private MultiModalMessage createSystemMessage() {
        return MultiModalMessage.builder()
                .role(SYSTEM)
                .content(List.of(Collections.singletonMap(TEXT, PROMPT)))
                .build();
    }

    private MultiModalMessage createUserMessage(String url) {
        return MultiModalMessage.builder()
                .role(USER)
                .content(Arrays.asList(Collections.singletonMap(IMAGE, url), Collections.singletonMap(TEXT, REVIEW_PROMPT)))
                .build();
    }

    public LLMReviewResult callWithRemoteImage(String url) {
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(apiKey)
                .model(MODEL)
                .messages(Arrays.asList(createSystemMessage(), createUserMessage(url)))
                .build();

        try {
            MultiModalConversationResult result = conv.call(param);
            MultiModalConversationUsage usage = result.getUsage();
            log.info("usage: {}", gson.toJson(usage));
            mqService.sendMessage("usage", usage);

            // Extract model output
            String modelOut = result.getOutput().getChoices().getFirst().getMessage().getContent().getFirst().get(TEXT).toString();
            String str;
            if (modelOut.startsWith("```json") && modelOut.endsWith("```")) {
                str = modelOut.substring(7, modelOut.length() - 3).trim();
            } else {
                str = modelOut.trim();
            }
            log.debug("modelOut: {}", str);
            LLMReviewResult.Builder builder = LLMReviewResult.newBuilder();
            JsonFormat.parser().merge(str, builder);
            return builder.build();

        } catch (Exception e) {
            log.error("Error calling LLM API: ", e);
            return LLMReviewResult.newBuilder().setOutcome(ReviewOutcome.UNDECIDED).setFailureReason("LLM API call failed").build();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void run() {
        mqService.receiveMessage(MEMES_QUEUE, Submission.class)
                .ifPresent(this::processSubmission);
    }

    private void processSubmission(Submission submission) {
        if (submission.getSubmissionType() == SubmissionType.IMAGE) {
            try {
                LLMReviewResult result = reviewImageSubmission(submission);
                mqService.sendMessage(REVIEWER_RESULT_QUEUE, result);
                handleReviewOutcome(submission.getId(), result.getOutcome());
            } catch (Exception e) {
                log.error("Error processing submission {}: {}", submission.getId(), e.getMessage(), e);
            }
        }
    }

    private LLMReviewResult reviewImageSubmission(Submission submission) {
        LLMReviewResult result = callWithRemoteImage(submission.getUrl());
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
                reviewService.reviewSubmission(submissionId, ACCEPT_ACTION);
                break;
            case UNDECIDED:
            case FAIL:
                log.info("{} is undecided or failed, will be reviewed manually", submissionId);
                break;
            default:
                log.error("{} has an unknown outcome, will be reviewed manually", submissionId);
        }
    }
}
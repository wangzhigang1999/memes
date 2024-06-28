package com.memes.service.submission;

import com.memes.model.common.PageResult;
import com.memes.model.param.ListSubmissionsRequest;
import com.memes.model.submission.Submission;

import java.io.InputStream;
import java.util.List;

public interface SubmissionService {

    /**
     * 存储文本类型的投稿
     */
    Submission storeTextFormatSubmission(String text, String mime);

    /**
     * 存储图片、视频类型的投稿
     */
    Submission storeStreamSubmission(InputStream stream, String mime);

    /**
     * 获取被标记为删除的投稿
     */
    List<Submission> getDeletedSubmission();

    /**
     * 标记删除
     */
    boolean markDelete(String id);

    /**
     * 硬删除
     */
    void hardDelete(String id);

    /**
     * 分页查询
     */
    PageResult<Submission> listSubmissions(ListSubmissionsRequest request);

    /**
     * id 查询
     */
    Submission getSubmissionById(String id);

    /**
     * 向量检索
     */
    List<Submission> getSimilarSubmission(String id, int size);

    /**
     * 随机获取投稿
     */
    List<Submission> randomSubmission(int size);

    /**
     * 反馈
     */
    boolean feedback(String id, String feedback);

}

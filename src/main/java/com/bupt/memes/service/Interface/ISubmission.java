package com.bupt.memes.service.Interface;

import com.bupt.memes.model.common.PageResult;
import com.bupt.memes.model.media.Submission;

import java.io.InputStream;
import java.util.List;

public interface ISubmission {


    /**
     * 点赞或者点踩
     *
     * @param id 对应投稿的名字
     * @param up true为点赞，false为点踩
     * @return 是否成功
     */
    boolean vote(String id, boolean up);


    /**
     * 存储文本类型的投稿
     *
     * @param url  url
     * @param mime mime
     * @return Submission
     */
    Submission storeTextFormatSubmission(String url, String mime);


    /**
     * 存储图片、视频类型的投稿
     *
     * @param stream 输入流
     * @param mime   mime
     * @return Submission
     */
    Submission storeStreamSubmission(InputStream stream, String mime);


    /**
     * 获取被标记为删除的投稿
     */
    List<Submission> getDeletedSubmission();


    /**
     * 硬删除
     */
    void hardDeleteSubmission(String id);


    PageResult<Submission> getSubmissionByPage(int pageSize, String lastID);

    Submission getSubmissionById(String id);

    List<Submission> getSubmissionByDate(String date);

}

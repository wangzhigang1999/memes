package com.bupt.dailyhaha.service.Interface;

import com.bupt.dailyhaha.pojo.common.PageResult;

import java.io.InputStream;
import java.util.List;

public interface Submission {


    /**
     * 点赞或者点踩
     *
     * @param hashcode 对应投稿的名字
     * @param up       true为点赞，false为点踩
     * @return 是否成功
     */
    boolean vote(int hashcode, boolean up);


    /**
     * 存储文本类型的投稿
     *
     * @param url  url
     * @param mime mime
     * @return Submission
     */
    com.bupt.dailyhaha.pojo.media.Submission storeTextFormatSubmission(String url, String mime);


    /**
     * 存储图片、视频类型的投稿
     *
     * @param stream   输入流
     * @param mime     mime
     * @param personal 是否是个人投稿
     * @return Submission
     */
    com.bupt.dailyhaha.pojo.media.Submission storeStreamSubmission(InputStream stream, String mime, boolean personal);


    /**
     * 获取被标记为删除的投稿
     */
    List<com.bupt.dailyhaha.pojo.media.Submission> getDeletedSubmission();


    /**
     * 硬删除
     */
    void hardDeleteSubmission(int hashcode);


    PageResult<com.bupt.dailyhaha.pojo.media.Submission> getSubmissionByPage(int pageNum, int pageSize, String lastID);

}

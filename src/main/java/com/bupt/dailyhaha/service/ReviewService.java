package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Submission;

import java.util.List;

public interface ReviewService {

    /**
     * 列出从上一天的22：00到现在的所有未审核的submission
     *
     * @return submission列表
     */
    List<Submission> listSubmissions();

    /**
     * 接受一个submission
     *
     * @param hashcode submission的hashcode
     * @return 是否接受成功
     */
    boolean acceptSubmission(int hashcode);

    /**
     * 拒绝一个submission
     *
     * @param hashcode submission的hashcode
     * @return 是否拒绝成功
     */
    boolean rejectSubmission(int hashcode);


    boolean release();

}

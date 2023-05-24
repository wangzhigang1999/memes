package com.bupt.dailyhaha.mapper;

import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.media.Submission;

import java.util.List;

public interface MSubmission {

    Submission create(Submission submission);

    Submission findByHash(Integer hash);

    List<Submission> find(Boolean deleted, Boolean reviewed);

    List<Submission> find(long from, long to, Boolean deleted, Boolean reviewed);

    Long count(long from, long to, Boolean deleted, Boolean reviewed);

    PageResult<Submission> find(int pageNum, int pageSize, String lastID);

    Long updateStatus(int hashcode, boolean deleted);


    boolean vote(int hashcode, boolean up);

    boolean hardDeleteByHash(Integer hash);

}

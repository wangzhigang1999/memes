package com.bupt.dailyhaha.mapper.impl;

import com.bupt.dailyhaha.mapper.MSubmission;
import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.media.Submission;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MSubmissionImpl implements MSubmission {
    @Override
    public Submission create(Submission submission) {
        return null;
    }

    @Override
    public Submission findByHash(Integer hash) {
        return null;
    }

    @Override
    public List<Submission> find(Boolean deleted, Boolean reviewed) {
        return null;
    }

    @Override
    public List<Submission> find(long from, long to, Boolean deleted, Boolean reviewed) {
        return null;
    }

    @Override
    public Long count(long from, long to, Boolean deleted, Boolean reviewed) {
        return null;
    }

    @Override
    public PageResult<Submission> find(int pageNum, int pageSize, String lastID) {
        return null;
    }

    @Override
    public Long updateStatus(int hashcode, boolean deleted) {
        return null;
    }

    @Override
    public boolean vote(int hashcode, boolean up) {
        return false;
    }

    @Override
    public boolean hardDeleteByHash(Integer hash) {
        return false;
    }
}

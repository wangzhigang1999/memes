package com.bupt.dailyhaha.mapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bupt.dailyhaha.mapper.MSubmission;
import com.bupt.dailyhaha.pojo.common.PageResult;
import com.bupt.dailyhaha.pojo.media.Submission;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubmissionMySQLImpl implements MSubmission {

    public static final int FIRST_PAGE_NUM = 1;
    final com.bupt.dailyhaha.mapper.mysql.MSubmission subMapper;


    public SubmissionMySQLImpl(com.bupt.dailyhaha.mapper.mysql.MSubmission subMapper) {
        this.subMapper = subMapper;
    }

    @Override
    public Submission create(Submission submission) {
        int insert = subMapper.insert(submission);
        if (insert > 0) {
            return submission;
        }
        return null;
    }

    @Override
    public Submission findByHash(Integer hash) {
        List<Submission> submissions = subMapper.selectByMap(Map.of("hash", hash));
        if (submissions.size() > 0) {
            return submissions.get(0);
        }
        return null;
    }

    @Override
    public List<Submission> find(Boolean deleted, Boolean reviewed) {
        return subMapper.find(0, System.currentTimeMillis(), bool2int(deleted), bool2int(reviewed));
    }

    @Override
    public List<Submission> find(long from, long to, Boolean deleted, Boolean reviewed) {
        return subMapper.find(from, to, bool2int(deleted), bool2int(reviewed));
    }

    @Override
    public Long count(long from, long to, Boolean deleted, Boolean reviewed) {
        return subMapper.count(from, to, bool2int(deleted), bool2int(reviewed));
    }

    @Override
    public PageResult<Submission> find(int pageNum, int pageSize, String lastID) {
        var total = count(0, System.currentTimeMillis(), false, true);
        final int pages = (int) Math.ceil(total / (double) pageSize);
        if (pageNum <= 0 || pageNum > pages) {
            pageNum = FIRST_PAGE_NUM;
        }

        QueryWrapper<Submission> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).eq("reviewed", 1);

        if (!StringUtils.isEmpty(lastID)) {
            wrapper.lt("id", lastID);
        }
        wrapper.orderByDesc("id");
        wrapper.last("limit " + pageSize);

        List<Submission> submissions = subMapper.selectList(wrapper);

        final PageResult<Submission> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPages(pages);
        pageResult.setPageSize(pageSize);
        pageResult.setPageNum(pageNum);
        pageResult.setList(submissions);
        return pageResult;
    }

    @Override
    public Long updateStatus(int hashcode, boolean deleted) {
        UpdateWrapper<Submission> wrapper = new UpdateWrapper<>();
        wrapper.set("deleted", bool2int(deleted)).
                set("reviewed", 1)
                .eq("hash", hashcode);
        return (long) subMapper.update(null, wrapper);
    }

    @Override
    public boolean vote(int hashcode, boolean up) {
        UpdateWrapper<Submission> wrapper = new UpdateWrapper<>();

        if (up) {
            // add up field
            wrapper.setSql("up = up + 1")
                    .eq("hash", hashcode);
        } else {
            // add down field
            wrapper = new UpdateWrapper<>();
            wrapper.setSql("down = down + 1")
                    .eq("hash", hashcode);
        }
        return subMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean hardDeleteByHash(Integer hash) {
        return subMapper.deleteByMap(Map.of("hash", hash)) > 0;
    }

    private int bool2int(Boolean b) {
        return b ? 1 : 0;
    }
}

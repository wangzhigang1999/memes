package com.memes.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memes.mapper.MediaMapper;
import com.memes.mapper.SubmissionMapper;
import com.memes.model.pojo.MediaContent;
import com.memes.model.pojo.Submission;
import com.memes.service.ReviewService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    final MediaMapper mediaMapper;
    final SubmissionMapper submissionMapper;

    @Override
    public List<MediaContent> listPendingMediaContent(Integer limit) {
        QueryWrapper<MediaContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", MediaContent.ContentStatus.PENDING);
        // order by time asc
        queryWrapper.orderByAsc("created_at");
        // limit
        queryWrapper.last("limit %d".formatted(limit));
        log.debug("queryWrapper: {}", queryWrapper);
        return this.mediaMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public boolean markMediaStatus(Integer id, MediaContent.ContentStatus status) {
        int updateById = mediaMapper.updateById(MediaContent.builder().status(status).build());
        // if approved, insert into submission table
        if (updateById > 0 && status == MediaContent.ContentStatus.APPROVED) {
            int insert = submissionMapper.insert(Submission.builder().mediaContentIdList(List.of(id)).build());
            return insert > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public int batchMarkMediaStatus(List<Integer> ids, MediaContent.ContentStatus status) {
        int update = mediaMapper
            .update(MediaContent.builder().status(status).build(), new QueryWrapper<MediaContent>().in("id", ids));
        // if approved, insert into submission table
        if (update > 0 && status == MediaContent.ContentStatus.APPROVED) {
            for (Integer id : ids) {
                submissionMapper.insert(Submission.builder().mediaContentIdList(List.of(id)).build());
            }
        }
        return update;
    }

    @Override
    public long getNumByStatusAndDate(MediaContent.ContentStatus status, String date) {
        return mediaMapper.selectCount(new QueryWrapper<MediaContent>().eq("status", status).like("created_at", date));
    }

    @Override
    public Submission mergeTwoSubmission(Integer first, Integer second) {
        Submission firstSub = submissionMapper.selectById(first);
        Submission secondSub = submissionMapper.selectById(second);
        if (firstSub != null && secondSub != null) {
            firstSub.getMediaContentIdList().addAll(secondSub.getMediaContentIdList());
            firstSub.getMediaContentIdList().sort(Integer::compareTo);
            firstSub.getTags().addAll(secondSub.getTags());
            submissionMapper.updateById(firstSub);
            submissionMapper.deleteById(second);
            return firstSub;
        }
        return null;
    }
}

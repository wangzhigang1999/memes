package com.memes.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.mapper.MediaMapper;
import com.memes.mapper.SubmissionMapper;
import com.memes.model.pojo.MediaContent;
import com.memes.model.pojo.Submission;
import com.memes.service.MediaContentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MediaContentServiceImpl extends ServiceImpl<MediaMapper, MediaContent> implements MediaContentService {
    private final MediaMapper mediaMapper;
    private final SubmissionMapper submissionMapper;

    public MediaContentServiceImpl(MediaMapper mediaMapper, SubmissionMapper submissionMapper) {
        this.mediaMapper = mediaMapper;
        this.submissionMapper = submissionMapper;
    }

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
        int updateById = mediaMapper.updateById(MediaContent.builder().id(id).status(status).build());
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
}

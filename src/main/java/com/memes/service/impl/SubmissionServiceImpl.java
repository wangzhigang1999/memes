package com.memes.service.impl;

import static com.google.common.collect.Sets.union;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.exception.AppException;
import com.memes.mapper.MediaMapper;
import com.memes.mapper.SubmissionMapper;
import com.memes.model.pojo.MediaContent;
import com.memes.model.pojo.Submission;
import com.memes.service.SubmissionService;
import com.memes.util.Preconditions;
import com.memes.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {
    private final SubmissionMapper submissionMapper;
    private final MediaMapper mediaMapper;

    public SubmissionServiceImpl(SubmissionMapper submissionMapper, MediaMapper mediaMapper) {
        this.submissionMapper = submissionMapper;
        this.mediaMapper = mediaMapper;
    }

    @Override
    public Submission mergeTwoSubmission(Long first, Long second) {
        Submission firstSub = submissionMapper.selectById(first);
        Submission secondSub = submissionMapper.selectById(second);
        if (firstSub != null && secondSub != null) {
            firstSub.getMediaContentIdList().addAll(secondSub.getMediaContentIdList());
            firstSub.getMediaContentIdList().sort(Long::compareTo);

            Set<String> firstTags = firstSub.getTags() != null ? firstSub.getTags() : Collections.emptySet();
            Set<String> secondTags = secondSub.getTags() != null ? secondSub.getTags() : Collections.emptySet();
            Set<String> mergedTags = union(firstTags, secondTags);
            firstSub.setTags(mergedTags);
            submissionMapper.updateById(firstSub);
            submissionMapper.deleteById(second);
            fillMediaContent(firstSub);
            return firstSub;
        }
        return null;
    }

    public Submission updateSubmissionCount(Long id, boolean isLike) {
        LambdaUpdateWrapper<Submission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Submission::getId, id);
        if (isLike) {
            updateWrapper.setSql("likes_count = likes_count + 1");
        } else {
            updateWrapper.setSql("dislikes_count = dislikes_count + 1");
        }
        log.info("Updating submission count for submission ID: {}", id);
        int update = submissionMapper.update(updateWrapper);
        Preconditions.checkArgument(update > 0, () -> AppException.databaseError("submission"));

        return submissionMapper.selectById(id); // 返回最新的 Submission
    }

    @Override
    public List<Submission> list(Integer querySize, Long lastId, String date) {
        // 主查询构建
        LambdaQueryWrapper<Submission> queryWrapper = new LambdaQueryWrapper<>();
        if (lastId != null && lastId > 0) {
            queryWrapper.lt(Submission::getId, lastId);
        }
        if (StringUtils.isNotEmpty(date)) {
            LocalDateTime startTime = TimeUtil.convertYMDToLocalDateTime(date);
            LocalDateTime endTime = startTime.plusDays(1);
            queryWrapper.between(Submission::getCreatedAt, startTime, endTime);
        }
        queryWrapper.orderByDesc(Submission::getId);

        // 执行主查询
        Page<Submission> submissionPage = submissionMapper.selectPage(new Page<>(1, querySize), queryWrapper);
        List<Submission> submissions = submissionPage.getRecords();

        // 如果没有记录，直接返回
        if (submissions.isEmpty()) {
            return submissions;
        }

        // 收集所有媒体内容ID，一次性查询
        Set<Long> allMediaIds = submissions
            .stream()
            .flatMap(s -> s.getMediaContentIdList().stream())
            .collect(Collectors.toSet());

        // 如果没有媒体ID，则直接返回
        if (allMediaIds.isEmpty()) {
            return List.of();
        }

        // 一次性查询所有媒体内容
        QueryWrapper<MediaContent> wrapper = new QueryWrapper<>();
        wrapper.in("id", allMediaIds);
        wrapper.select("id", "data_type", "data_content", "user_id", "created_at", "updated_at");
        List<MediaContent> allMediaContents = mediaMapper.selectList(wrapper);

        // 构建媒体内容的Map，便于快速查找
        Map<Long, MediaContent> mediaContentMap = allMediaContents
            .stream()
            .collect(Collectors.toMap(MediaContent::getId, mc -> mc));

        // 使用虚拟线程并行处理每个提交
        List<CompletableFuture<Void>> futures = submissions
            .stream()
            .map(submission -> CompletableFuture.runAsync(() -> {
                List<MediaContent> submissionMedias = submission
                    .getMediaContentIdList()
                    .stream()
                    .map(mediaContentMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                submission.setMediaContentList(submissionMedias);
            }, Executors.newVirtualThreadPerTaskExecutor()))
            .toList();

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return submissions;
    }

    private void fillMediaContent(Submission submission) {
        List<Long> mediaContentIdList = submission.getMediaContentIdList();
        QueryWrapper<MediaContent> wrapper = new QueryWrapper<>();
        wrapper.in("id", mediaContentIdList);
        // ignore llm moderation status
        wrapper.select("id", "data_type", "data_content", "user_id", "created_at", "updated_at");
        List<MediaContent> mediaContents = mediaMapper.selectList(wrapper);
        submission.setMediaContentList(mediaContents);
    }

    @Override
    public Submission getById(Serializable id) {
        Submission byId = super.getById(id);
        fillMediaContent(byId);
        return byId;
    }

}

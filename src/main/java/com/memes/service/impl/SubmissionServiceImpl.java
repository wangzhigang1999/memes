package com.memes.service.impl;

import static com.google.common.collect.Sets.union;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.aspect.Audit;
import com.memes.exception.AppException;
import com.memes.mapper.MediaMapper;
import com.memes.mapper.PinnedSubmissionMapper;
import com.memes.mapper.SubmissionMapper;
import com.memes.model.pojo.MediaContent;
import com.memes.model.pojo.PinnedSubmission;
import com.memes.model.pojo.Submission;
import com.memes.service.SubmissionService;
import com.memes.util.Preconditions;
import com.memes.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {
    private final SubmissionMapper submissionMapper;
    private final PinnedSubmissionMapper pinnedSubmissionMapper;
    private final MediaMapper mediaMapper;

    public SubmissionServiceImpl(SubmissionMapper submissionMapper, PinnedSubmissionMapper pinnedSubmissionMapper, MediaMapper mediaMapper) {
        this.submissionMapper = submissionMapper;
        this.pinnedSubmissionMapper = pinnedSubmissionMapper;
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
    public List<Submission> list(Integer querySize, Long lastId, String date, boolean random) {
        // 参数验证精简
        Preconditions
            .checkArgument(
                querySize != null && querySize > 0 && querySize < 50,
                AppException.invalidParam("querySize must be between 1 and 50"));

        // 主查询逻辑
        List<Submission> submissions = random ? getRandomSubmissions(querySize) : queryPaginatedSubmissions(querySize, lastId, date);

        if (submissions.isEmpty()) {
            return submissions;
        }

        // 媒体内容处理
        populateMediaContents(submissions);

        return submissions;
    }

    private List<Submission> queryPaginatedSubmissions(Integer querySize, Long lastId, String date) {
        LambdaQueryWrapper<Submission> queryWrapper = new LambdaQueryWrapper<>();

        if (lastId != null && lastId > 0) {
            queryWrapper.lt(Submission::getId, lastId);
        }

        if (StringUtils.isNotEmpty(date)) {
            LocalDateTime startTime = TimeUtil.convertYMDToLocalDateTime(date);
            queryWrapper.between(Submission::getCreatedAt, startTime, startTime.plusDays(1));
        }

        queryWrapper.orderByDesc(Submission::getId);
        return submissionMapper.selectPage(new Page<>(1, querySize), queryWrapper).getRecords();
    }

    private void populateMediaContents(List<Submission> submissions) {
        Set<Long> mediaIds = submissions
            .stream()
            .flatMap(s -> s.getMediaContentIdList().stream())
            .collect(Collectors.toSet());

        if (mediaIds.isEmpty()) {
            return;
        }

        Map<Long, MediaContent> mediaMap = fetchMediaContents(mediaIds);

        // 使用并行流替代虚拟线程，除非确实需要更轻量级的线程
        submissions.parallelStream().forEach(submission -> {
            List<MediaContent> submissionMedias = submission
                .getMediaContentIdList()
                .stream()
                .map(mediaMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            submission.setMediaContentList(submissionMedias);
        });
    }

    private Map<Long, MediaContent> fetchMediaContents(Set<Long> mediaIds) {
        // 使用LambdaQueryWrapper更简洁
        return mediaMapper
            .selectList(
                new LambdaQueryWrapper<MediaContent>()
                    .in(MediaContent::getId, mediaIds)
                    .select(
                        MediaContent::getId,
                        MediaContent::getDataType,
                        MediaContent::getDataContent,
                        MediaContent::getUserId,
                        MediaContent::getCreatedAt,
                        MediaContent::getUpdatedAt))
            .stream()
            .collect(Collectors.toMap(MediaContent::getId, Function.identity()));
    }

    @Override
    public boolean pinSubmission(Long subId) {
        PinnedSubmission pinnedSubmission = PinnedSubmission
            .builder()
            .submissionId(subId)
            .createdBy(Audit.getCurrentUuid())
            .build();
        if (pinnedSubmissionMapper.selectCount(new LambdaQueryWrapper<PinnedSubmission>().eq(PinnedSubmission::getSubmissionId, subId)) > 0) {
            throw new AppException("submission already pinned");
        }
        return pinnedSubmissionMapper.insert(pinnedSubmission) > 0;
    }

    @Override
    public boolean unpinSubmission(Long subId) {
        return pinnedSubmissionMapper.delete(new LambdaQueryWrapper<PinnedSubmission>().eq(PinnedSubmission::getSubmissionId, subId)) > 0;
    }

    public List<Submission> listPinnedSubmission() {
        List<PinnedSubmission> pinnedSubmissions = pinnedSubmissionMapper.selectList(null);
        List<Long> submissionIds = pinnedSubmissions.stream().map(PinnedSubmission::getSubmissionId).distinct().toList();
        if (submissionIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Submission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Submission::getId, submissionIds);
        List<Submission> submissions = submissionMapper.selectList(queryWrapper);
        for (Submission submission : submissions) {
            if (submission.getTags() == null) {
                submission.setTags(new HashSet<>());
            }
            submission.getTags().add("pinned");
            fillMediaContent(submission);
        }
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

    public List<Submission> getRandomSubmissions(int num) {
        // 获取所有记录的数量
        long totalRecords = count();
        if (totalRecords == 0 || num <= 0) {
            return List.of(); // 返回空列表
        }
        // 计算需要查询的记录数
        int recordsToFetch = Math.min(num, (int) totalRecords);
        // 创建随机ID集合
        Random random = new Random();
        List<Long> randomIds = new ArrayList<>();
        while (randomIds.size() < recordsToFetch) {
            long randomId = (long) (random.nextDouble() * totalRecords + 1);
            if (!randomIds.contains(randomId)) {
                randomIds.add(randomId);
            }
        }
        // 使用QueryWrapper查询这些随机ID
        QueryWrapper<Submission> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", randomIds);
        return list(queryWrapper);
    }

}

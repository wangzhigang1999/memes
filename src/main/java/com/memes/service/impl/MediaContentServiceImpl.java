package com.memes.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.aspect.Audit;
import com.memes.exception.AppException;
import com.memes.mapper.MediaMapper;
import com.memes.mapper.SubmissionMapper;
import com.memes.model.common.FileUploadResult;
import com.memes.model.pojo.MediaContent;
import com.memes.model.pojo.Submission;
import com.memes.service.MediaContentService;
import com.memes.service.StorageService;
import com.memes.util.HashUtil;
import com.memes.util.Preconditions;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MediaContentServiceImpl extends ServiceImpl<MediaMapper, MediaContent> implements MediaContentService {
    private final MediaMapper mediaMapper;
    private final SubmissionMapper submissionMapper;
    private final StorageService storageService;

    public MediaContentServiceImpl(MediaMapper mediaMapper, SubmissionMapper submissionMapper, StorageService storageService) {
        this.mediaMapper = mediaMapper;
        this.submissionMapper = submissionMapper;
        this.storageService = storageService;
    }

    @Override
    public List<MediaContent> listPendingMediaContent(Integer limit) {
        QueryWrapper<MediaContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", MediaContent.ContentStatus.PENDING);
        queryWrapper.eq("llm_moderation_status", MediaContent.AiModerationStatus.PENDING);
        // order by time asc
        queryWrapper.orderByAsc("created_at");
        // limit
        queryWrapper.last("limit %d".formatted(limit));
        log.debug("queryWrapper: {}", queryWrapper);
        return this.mediaMapper.selectList(queryWrapper);
    }

    @Override
    public List<MediaContent> listNoSharpReviewMediaContent(Integer limit) {
        QueryWrapper<MediaContent> queryWrapper = new QueryWrapper<>();
        // 过去 3 天的内容
        queryWrapper.ge("created_at", LocalDateTime.now().minusDays(7));
        // llm_description 不为空
        queryWrapper.isNotNull("llm_description");
        // sharp_review 为空
        queryWrapper.isNull("sharp_review");
        // not rejected
        queryWrapper.eq("status", MediaContent.ContentStatus.APPROVED);
        // order by time asc
        queryWrapper.orderByDesc("id");
        // limit
        queryWrapper.last("limit %d".formatted(limit));
        log.debug("queryWrapper: {}", queryWrapper);
        return this.mediaMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public boolean markMediaStatus(Long id, MediaContent.ContentStatus status) {
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
    public int batchMarkMediaStatus(List<Long> ids, MediaContent.ContentStatus status) {
        int update = mediaMapper
            .update(MediaContent.builder().status(status).build(), new QueryWrapper<MediaContent>().in("id", ids));
        // if approved, insert into submission table
        if (update > 0 && status == MediaContent.ContentStatus.APPROVED) {
            for (Long id : ids) {
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
    public MediaContent storeTextFormatSubmission(String text, String mime) {
        String uniqueCode = HashUtil.strToHex(text, HashUtil.HashAlgorithm.MD5);
        MediaContent mediaContent = mediaMapper.selectOne(new QueryWrapper<MediaContent>().eq("checksum", uniqueCode));
        if (mediaContent != null) {
            log.info("MediaContent already exists with checksum: {}", uniqueCode);
            return mediaContent;
        }
        mediaContent = MediaContent
            .builder()
            .dataType(MediaContent.DataType.MARKDOWN)
            .dataContent(text)
            .checksum(uniqueCode)
            .fileSize((long) text.getBytes().length)
            .userId(Audit.getCurrentUuid())
            .build();
        int insert = mediaMapper.insert(mediaContent);
        Preconditions.checkArgument(insert > 0, AppException.databaseError("insert media content failed"));
        return mediaContent;
    }

    @SneakyThrows
    @Override
    public MediaContent storeStreamSubmission(InputStream inputStream, String mime) {
        byte[] bytes = inputStream.readAllBytes();
        String uniqueCode = HashUtil.bytesToHex(bytes, HashUtil.HashAlgorithm.MD5);
        MediaContent mediaContent = mediaMapper.selectOne(new QueryWrapper<MediaContent>().eq("checksum", uniqueCode));
        if (mediaContent != null) {
            log.warn("MediaContent already exists with checksum: {}", uniqueCode);
            return mediaContent;
        }

        FileUploadResult store = storageService.store(bytes, mime);
        Preconditions.checkNotNull(store, AppException.storageError("file upload failed,type:%s".formatted(mime)));
        mediaContent = MediaContent
            .builder()
            .dataType(MediaContent.DataType.valueOf(mime.split("/")[0].toUpperCase()))
            .dataContent(store.url())
            .checksum(uniqueCode)
            .fileSize(((long) bytes.length))
            .userId(Audit.getCurrentUuid())
            .build();
        int insert = mediaMapper.insert(mediaContent);
        Preconditions.checkArgument(insert > 0, AppException.databaseError("insert media content failed"));
        return mediaContent;
    }
}

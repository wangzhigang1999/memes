package com.memes.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memes.exception.AppException;
import com.memes.model.pojo.MediaContent;
import com.memes.service.MediaContentService;
import com.memes.util.Preconditions;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaContentController {

    private final MediaContentService mediaContentService;

    @GetMapping("/status/{status}")
    public List<MediaContent> listWithStatus(@PathVariable MediaContent.ContentStatus status, Integer limit, Long lastId) {
        Preconditions.checkArgument(status != null, AppException.invalidParam("status"));
        if (limit != null) {
            limit = Math.min(limit, 100);
        } else {
            limit = 100;
        }
        LambdaQueryWrapper<MediaContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MediaContent::getStatus, status);
        wrapper.orderByDesc(MediaContent::getCreatedAt);
        if (lastId != null) {
            wrapper.lt(MediaContent::getId, lastId);
        }
        return mediaContentService.list(new Page<>(0, limit), wrapper);
    }

    @PostMapping("/{id}/status/{status}")
    public boolean markStatus(@PathVariable Long id, @PathVariable MediaContent.ContentStatus status) {
        return mediaContentService.markMediaStatus(id, status);
    }

    @PostMapping("/batch/status/{status}")
    public int batchMarkStatus(@RequestBody Long[] ids, @PathVariable MediaContent.ContentStatus status) {
        return mediaContentService.batchMarkMediaStatus(List.of(ids), status);
    }

    @PostMapping("")
    public MediaContent upload(MultipartFile file, String text, String mime) throws IOException {
        Preconditions.checkArgument(mime != null && !mime.isEmpty(), AppException.invalidParam("mime"));
        Preconditions.checkArgument(text != null || file != null, AppException.invalidParam("file or text"));
        MediaContent mediaContent;
        if (mime.startsWith("text")) {
            mediaContent = mediaContentService.storeTextFormatSubmission(text, mime);
        } else {
            Preconditions.checkArgument(file != null, AppException.invalidParam("file"));
            InputStream inputStream = file.getInputStream();
            mediaContent = mediaContentService.storeStreamSubmission(inputStream, mime);
            inputStream.close();
        }
        if (mediaContent.getDataType() == MediaContent.DataType.MARKDOWN) {
            mediaContent.setDataContent("Yay !");
        }
        return mediaContent;
    }

    @GetMapping("/{id}")
    public MediaContent getMedia(@PathVariable Long id) {
        MediaContent mediaContent = mediaContentService.getById(id);
        if (mediaContent == null) {
            throw AppException.resourceNotFound(String.valueOf(id));
        }
        return mediaContent;
    }
}

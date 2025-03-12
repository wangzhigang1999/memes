package com.memes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memes.annotation.AuthRequired;
import com.memes.model.pojo.MediaContent;
import com.memes.service.MediaContentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaContentController {

    private final MediaContentService mediaContentService;

    @AuthRequired
    @GetMapping("/{id}")
    public MediaContent getById(@PathVariable Integer id) {
        return mediaContentService.getById(id);
    }

    @GetMapping
    public Page<MediaContent> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return mediaContentService.page(new Page<>(page, size));
    }

    // list with status
    @GetMapping("/status/{status}")
    public Page<MediaContent> listWithStatus(@PathVariable MediaContent.ContentStatus status, @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size) {
        return mediaContentService.page(new Page<>(page, size), new LambdaQueryWrapper<MediaContent>().eq(MediaContent::getStatus, status));
    }

    @PutMapping("/{id}/status/{status}")
    public boolean markStatus(@PathVariable Integer id, @PathVariable MediaContent.ContentStatus status) {
        return mediaContentService.markMediaStatus(id, status);
    }

    @PutMapping("/{id}")
    public MediaContent update(@PathVariable Integer id, @RequestBody MediaContent mediaContent) {
        mediaContent.setId(id);
        mediaContentService.updateById(mediaContent);
        return mediaContent;
    }

    @PutMapping("/batch/status/{status}")
    public int batchMarkStatus(@RequestBody Integer[] ids, @PathVariable MediaContent.ContentStatus status) {
        return mediaContentService.batchMarkMediaStatus(List.of(ids), status);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return mediaContentService.removeById(id);
    }
}

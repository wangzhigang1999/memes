package com.memes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memes.model.pojo.MediaContent;
import com.memes.service.MediaContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaContentController {

    private final MediaContentService mediaContentService;

    @GetMapping("/{id}")
    public MediaContent getById(@PathVariable Integer id) {
        return mediaContentService.getById(id);
    }

    @GetMapping
    public Page<MediaContent> list(@RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size) {
        return mediaContentService.page(new Page<>(page, size));
    }

    @PutMapping("/{id}")
    public MediaContent update(@PathVariable Integer id, @RequestBody MediaContent mediaContent) {
        mediaContent.setId(id);
        mediaContentService.updateById(mediaContent);
        return mediaContent;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        mediaContentService.removeById(id);
    }

    @GetMapping("/search")
    public Page<MediaContent> search(@RequestParam(required = false) String userId,
        @RequestParam(required = false) MediaContent.DataType dataType, @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size) {

        LambdaQueryWrapper<MediaContent> queryWrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            queryWrapper.eq(MediaContent::getUserId, userId);
        }

        if (dataType != null) {
            queryWrapper.eq(MediaContent::getDataType, dataType);
        }

        return mediaContentService.page(new Page<>(page, size), queryWrapper);
    }
}

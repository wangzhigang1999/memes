package com.memes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memes.annotation.AuthRequired;
import com.memes.model.pojo.MediaContent;
import com.memes.service.MediaContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media Content API", description = "Media content management operations")
public class MediaContentController {

    private final MediaContentService mediaContentService;

    @Operation(summary = "Get media content by ID", description = "Retrieve a specific media content by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Media content found", content = @Content(schema = @Schema(implementation = MediaContent.class))),
        @ApiResponse(responseCode = "404", description = "Media content not found")
    })
    @AuthRequired
    @GetMapping("/{id}")
    public MediaContent getById(
        @Parameter(description = "ID of the media content", required = true) @PathVariable Integer id) {
        return mediaContentService.getById(id);
    }

    @Operation(summary = "List media contents", description = "Get a paginated list of all media contents")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public Page<MediaContent> list(
        @Parameter(description = "Page number", required = false) @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "Page size", required = false) @RequestParam(defaultValue = "10") Integer size) {
        return mediaContentService.page(new Page<>(page, size));
    }

    @Operation(summary = "List media contents by status", description = "Get a paginated list of media contents filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/status/{status}")
    public Page<MediaContent> listWithStatus(
        @Parameter(description = "Status to filter by", required = true) @PathVariable MediaContent.ContentStatus status,
        @Parameter(description = "Page number", required = false) @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "Page size", required = false) @RequestParam(defaultValue = "10") Integer size) {
        return mediaContentService
            .page(
                new Page<>(page, size),
                new LambdaQueryWrapper<MediaContent>().eq(MediaContent::getStatus, status));
    }

    @Operation(summary = "Update media content status", description = "Update the status of a specific media content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Media content not found")
    })
    @PutMapping("/{id}/status/{status}")
    public boolean markStatus(
        @Parameter(description = "ID of the media content", required = true) @PathVariable Integer id,
        @Parameter(description = "New status to set", required = true) @PathVariable MediaContent.ContentStatus status) {
        return mediaContentService.markMediaStatus(id, status);
    }

    @Operation(summary = "Update media content", description = "Update an existing media content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Media content updated successfully", content = @Content(schema = @Schema(implementation = MediaContent.class))),
        @ApiResponse(responseCode = "404", description = "Media content not found")
    })
    @PutMapping("/{id}")
    public MediaContent update(
        @Parameter(description = "ID of the media content", required = true) @PathVariable Integer id,
        @Parameter(description = "Updated media content data", required = true) @RequestBody MediaContent mediaContent) {
        mediaContent.setId(id);
        mediaContentService.updateById(mediaContent);
        return mediaContent;
    }

    @Operation(summary = "Batch update media content status", description = "Update the status of multiple media contents")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statuses updated successfully")
    })
    @PutMapping("/batch/status/{status}")
    public int batchMarkStatus(
        @Parameter(description = "Array of media content IDs", required = true) @RequestBody Integer[] ids,
        @Parameter(description = "New status to set", required = true) @PathVariable MediaContent.ContentStatus status) {
        return mediaContentService.batchMarkMediaStatus(List.of(ids), status);
    }

    @Operation(summary = "Delete media content", description = "Delete a specific media content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Media content deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Media content not found")
    })
    @DeleteMapping("/{id}")
    public boolean delete(
        @Parameter(description = "ID of the media content to delete", required = true) @PathVariable Integer id) {
        return mediaContentService.removeById(id);
    }
}

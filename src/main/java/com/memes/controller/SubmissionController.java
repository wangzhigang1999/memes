package com.memes.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memes.model.pojo.Submission;
import com.memes.service.SubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submission API", description = "Submission management operations")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "Create submission", description = "Create a new submission")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Submission created successfully", content = @Content(schema = @Schema(implementation = Submission.class))),
        @ApiResponse(responseCode = "400", description = "Invalid submission data")
    })
    @PostMapping
    public Submission create(
        @Parameter(description = "Submission data to create", required = true) @RequestBody Submission submission) {
        submissionService.save(submission);
        return submission;
    }

    @Operation(summary = "Get submission by ID", description = "Retrieve a specific submission by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Submission found", content = @Content(schema = @Schema(implementation = Submission.class))),
        @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @GetMapping("/{id}")
    public Submission getById(
        @Parameter(description = "ID of the submission", required = true) @PathVariable Integer id) {
        return submissionService.getById(id);
    }

    @Operation(summary = "List submissions", description = "Get a paginated list of all submissions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public Page<Submission> list(
        @Parameter(description = "Page number", required = false) @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "Page size", required = false) @RequestParam(defaultValue = "10") Integer size) {
        return submissionService.page(new Page<>(page, size));
    }

    @Operation(summary = "Update submission", description = "Update an existing submission")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Submission updated successfully", content = @Content(schema = @Schema(implementation = Submission.class))),
        @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @PutMapping("/{id}")
    public Submission update(
        @Parameter(description = "ID of the submission", required = true) @PathVariable Integer id,
        @Parameter(description = "Updated submission data", required = true) @RequestBody Submission submission) {
        submission.setId(id);
        submissionService.updateById(submission);
        return submission;
    }

    @Operation(summary = "Delete submission", description = "Delete a specific submission")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Submission deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @DeleteMapping("/{id}")
    public void delete(
        @Parameter(description = "ID of the submission to delete", required = true) @PathVariable Integer id) {
        submissionService.removeById(id);
    }
}

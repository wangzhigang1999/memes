package com.memes.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memes.annotation.AuthRequired;
import com.memes.model.response.VisitStatistic;
import com.memes.service.impl.AdminService;
import com.memes.util.TimeUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "Administrative operations and statistics")
public class AdminController {

    AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Validate admin authentication", description = "Check if the current user has admin privileges")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin authentication successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an admin")
    })
    @AuthRequired
    @GetMapping("/validate")
    public boolean validate() {
        return true;
    }

    @Operation(summary = "Get visit statistics", description = "Retrieve visit statistics for a specific date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", content = @Content(schema = @Schema(implementation = VisitStatistic.class))),
        @ApiResponse(responseCode = "400", description = "Invalid date format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an admin")
    })
    @AuthRequired
    @GetMapping("/visit/statistic")
    public VisitStatistic visitStat(
        @Parameter(description = "Date in YYYY-MM-DD format. If not provided, current date will be used") @RequestParam(value = "date", required = false) String date) {
        if (date == null) {
            date = TimeUtil.getYMD();
        }
        return this.adminService.getVisitStatistic(date);
    }

    @Operation(summary = "Get review statistics", description = "Retrieve statistics about content reviews")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not an admin")
    })
    @AuthRequired
    @GetMapping("/review/statistic")
    public Map<String, Long> reviewStat() {
        return this.adminService.getReviewStatistic();
    }
}

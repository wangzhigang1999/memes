package com.memes.controller;

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
import com.memes.model.pojo.Config;
import com.memes.service.ConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Tag(name = "Config API", description = "System configuration management operations")
public class ConfigController {

    private final ConfigService configService;

    @Operation(summary = "Get config by ID", description = "Retrieve a specific configuration item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Config found", content = @Content(schema = @Schema(implementation = Config.class))),
        @ApiResponse(responseCode = "404", description = "Config not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @AuthRequired
    @GetMapping("/{id}")
    public Config getById(
        @Parameter(description = "ID of the config item", required = true) @PathVariable String id) {
        return configService.getById(id);
    }

    @Operation(summary = "List configs", description = "Get a paginated list of all configuration items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @AuthRequired
    @GetMapping
    public Page<Config> list(
        @Parameter(description = "Page number", required = false) @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "Page size", required = false) @RequestParam(defaultValue = "10") Integer size,
        @Parameter(description = "Only show visible configs", required = false) @RequestParam(required = false) Boolean visibleOnly) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        if (Boolean.TRUE.equals(visibleOnly)) {
            queryWrapper.eq(Config::getVisible, true);
        }
        return configService.page(new Page<>(page, size), queryWrapper);
    }

    @Operation(summary = "Update config", description = "Update an existing configuration item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Config updated successfully", content = @Content(schema = @Schema(implementation = Config.class))),
        @ApiResponse(responseCode = "404", description = "Config not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @AuthRequired
    @PutMapping("/{id}")
    public Config update(
        @Parameter(description = "ID of the config item", required = true) @PathVariable Integer id,
        @Parameter(description = "Updated config data", required = true) @RequestBody Config config) {
        config.setId(id);
        configService.updateById(config);
        return config;
    }

    @Operation(summary = "Get config by key", description = "Retrieve a configuration item by its key")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Config found", content = @Content(schema = @Schema(implementation = Config.class))),
        @ApiResponse(responseCode = "404", description = "Config not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @AuthRequired
    @GetMapping("/value/{key}")
    public Config getConfig(
        @Parameter(description = "Key of the config item", required = true) @PathVariable String key) {
        return configService.getConfig(key);
    }

    @Operation(summary = "Update config value", description = "Update the value of a configuration item by its key")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Config value updated successfully"),
        @ApiResponse(responseCode = "404", description = "Config not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @AuthRequired
    @PutMapping("/value/{key}")
    public boolean updateConfigValue(
        @Parameter(description = "Key of the config item", required = true) @PathVariable String key,
        @Parameter(description = "New value to set", required = true) @RequestBody String value) {
        return configService.updateConfigValue(key, value);
    }
}

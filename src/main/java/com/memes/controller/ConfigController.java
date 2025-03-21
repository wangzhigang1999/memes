package com.memes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.memes.annotation.AuthRequired;
import com.memes.exception.AppException;
import com.memes.model.pojo.Config;
import com.memes.service.ConfigService;
import com.memes.util.Preconditions;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @AuthRequired
    @GetMapping("/{id}")
    public Config getById(@PathVariable String id) {
        return configService.getById(id);
    }

    @AuthRequired
    @GetMapping
    public List<Config> list() {
        return configService.listConfigs();
    }

    @AuthRequired
    @PostMapping("/{id}/{value}")
    public Config update(@PathVariable Long id, @PathVariable String value) {
        UpdateWrapper<Config> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("value", value);
        boolean update = configService.update(wrapper);
        Preconditions.checkArgument(update, () -> AppException.databaseError("config"));
        return configService.getById(id);
    }

    @AuthRequired
    @GetMapping("/value/{key}")
    public Config getConfig(@PathVariable String key) {
        return configService.getConfig(key);
    }
}

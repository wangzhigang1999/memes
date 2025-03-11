package com.memes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memes.annotation.AuthRequired;
import com.memes.model.pojo.Config;
import com.memes.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Page<Config> list(@RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) Boolean visibleOnly) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        if (Boolean.TRUE.equals(visibleOnly)) {
            queryWrapper.eq(Config::getVisible, true);
        }
        return configService.page(new Page<>(page, size), queryWrapper);
    }

    @AuthRequired
    @PutMapping("/{id}")
    public Config update(@PathVariable Integer id, @RequestBody Config config) {
        config.setId(id);
        configService.updateById(config);
        return config;
    }

    @AuthRequired
    @GetMapping("/value/{key}")
    public Config getConfig(@PathVariable String key) {
        return configService.getConfig(key);
    }

    @AuthRequired
    @PutMapping("/value/{key}")
    public boolean updateConfigValue(@PathVariable String key, @RequestBody String value) {
        return configService.updateConfigValue(key, value);
    }
}

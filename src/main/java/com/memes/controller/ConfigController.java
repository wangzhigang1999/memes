package com.memes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memes.annotation.AuthRequired;
import com.memes.model.pojo.ConfigItem;
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
    public ConfigItem getById(@PathVariable String id) {
        return configService.getById(id);
    }

    @AuthRequired
    @GetMapping
    public Page<ConfigItem> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) Boolean visibleOnly) {
        LambdaQueryWrapper<ConfigItem> queryWrapper = new LambdaQueryWrapper<>();
        if (Boolean.TRUE.equals(visibleOnly)) {
            queryWrapper.eq(ConfigItem::isVisible, true);
        }
        return configService.page(new Page<>(page, size), queryWrapper);
    }

    @AuthRequired
    @PutMapping("/{id}")
    public ConfigItem update(@PathVariable String id, @RequestBody ConfigItem configItem) {
        configItem.setId(id);
        configService.updateById(configItem);
        return configItem;
    }


    @AuthRequired
    @GetMapping("/value/{key}")
    public String getConfigValue(@PathVariable String key) {
        return configService.getConfigValue(key);
    }

    @AuthRequired
    @PutMapping("/value/{key}")
    public boolean updateConfigValue(@PathVariable String key, @RequestBody String value) {
        return configService.updateConfigValue(key, value);
    }
} 
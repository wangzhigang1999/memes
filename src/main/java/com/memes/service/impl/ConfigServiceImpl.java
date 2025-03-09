package com.memes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.mapper.ConfigMapper;
import com.memes.model.pojo.ConfigItem;
import com.memes.service.ConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, ConfigItem> implements ConfigService {

    @Override
    public String getConfigValue(String key) {
        LambdaQueryWrapper<ConfigItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConfigItem::getKey, key);
        ConfigItem configItem = getOne(queryWrapper);
        return configItem != null ? configItem.getValue() : null;
    }

    @Override
    @Transactional
    public boolean updateConfigValue(String key, String value) {
        LambdaQueryWrapper<ConfigItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConfigItem::getKey, key);
        ConfigItem configItem = getOne(queryWrapper);
        
        if (configItem != null) {
            configItem.setValue(value);
            return updateById(configItem);
        }
        return false;
    }
} 
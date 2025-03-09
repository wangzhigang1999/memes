package com.memes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memes.model.pojo.ConfigItem;

public interface ConfigService extends IService<ConfigItem> {
    /**
     * Get config value by key
     * @param key config key
     * @return config value
     */
    String getConfigValue(String key);

    /**
     * Update config value by key
     * @param key config key
     * @param value new value
     * @return true if updated successfully
     */
    boolean updateConfigValue(String key, String value);
} 
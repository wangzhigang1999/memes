package com.memes.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memes.model.pojo.Config;

public interface ConfigService extends IService<Config> {
    /**
     * Get config value by key
     *
     * @param key
     *            config key
     * @return config value
     */
    Config getConfig(String key);

    /**
     * Update config value by key
     *
     * @param key
     *            config key
     * @param value
     *            new value
     * @return true if updated successfully
     */
    boolean updateConfigValue(String key, String value);

    List<Config> listConfigs();
}

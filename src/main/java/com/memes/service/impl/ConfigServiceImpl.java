package com.memes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memes.mapper.ConfigMapper;
import com.memes.model.pojo.Config;
import com.memes.service.ConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    @Override
    public Config getConfig(String key) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getConfigKey, key);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateConfigValue(String key, String value) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getConfigKey, key);
        Config config = getOne(queryWrapper);

        if (config != null) {
            config.setValue(value);
            return updateById(config);
        }
        return false;
    }

    @Override
    public List<Config> listConfigs() {
        return list();
    }
}

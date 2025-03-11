package com.memes.service.impl;

import com.memes.mapper.ConfigMapper;
import com.memes.model.pojo.Config;
import com.memes.service.ConfigService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConfigServiceImplTest {

    @Autowired
    ConfigService configService;

    @Autowired
    ConfigMapper configMapper;
    private final Integer id = 123456;

    @BeforeEach
    void setUp() {
        configMapper
            .insert(
                Config
                    .builder()
                    .id(id)
                    .configKey("test")
                    .value("test")
                    .description("test")
                    .visible(false)
                    .visibleName("test")
                    .type(Config.Type.STRING)
                    .build());

    }

    @AfterEach
    void tearDown() {
        configService.removeById(id);
    }

    @Test
    void getConfig() {
        Config config = configService.getConfig("test");
        assert config.getConfigKey().equals("test");
    }

    @Test
    void updateConfigValue() {
        assert configService.updateConfigValue("test", "test2");
        assert configService.getConfig("test").getValue().equals("test2");
    }

    @Test
    void listConfigs() {
        assert !configService.listConfigs().isEmpty();
        assert configService.listConfigs().getFirst().getConfigKey().equals("test");
    }
}

package com.bupt.dailyhaha.mapper.impl;

import com.bupt.dailyhaha.mapper.MSys;
import com.bupt.dailyhaha.pojo.SysConfig;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@Service
public class SysLocalImpl implements MSys {

    final static String CONFIG_FILE = "memes.json";
    final static Gson gson = new Gson();
    Logger logger = org.slf4j.LoggerFactory.getLogger(SysLocalImpl.class);

    @Override
    @SneakyThrows
    public synchronized SysConfig load() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            logger.warn("Config file not found, return null.");
            return null;
        }

        FileReader reader = new FileReader(file);
        char[] chars = new char[1024];
        int read = reader.read(chars);
        String json = new String(chars, 0, read);
        reader.close();
        logger.info("Config file loaded.");
        return gson.fromJson(json, SysConfig.class);
    }

    @Override
    @SneakyThrows
    public synchronized SysConfig save(SysConfig sys) {
        File file = new File(CONFIG_FILE);
        assert file.exists() || file.createNewFile();

        String json = gson.toJson(sys);
        java.io.FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.close();
        logger.info("Config file saved.");
        return sys;
    }
}

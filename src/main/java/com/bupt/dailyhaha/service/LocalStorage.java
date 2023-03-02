package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.Image;
import com.bupt.dailyhaha.Storage;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static com.bupt.dailyhaha.Image.imageTypeCheck;

@Service("local")
public class LocalStorage implements Storage {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(LocalStorage.class);
    final CacheService cache;
    String localDir = "shadiao/";
    @Value("${local.urlPrefix}")
    String urlPrefix;

    public LocalStorage(CacheService cache) {
        File file = new File(localDir);
        if (!file.exists()) {
            file.mkdir();
        }
        this.cache = cache;
    }


    @Override
    public Image store(InputStream stream, boolean personal) {
        byte[] bytes;
        try {
            bytes = stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (cache.contains(Arrays.hashCode(bytes))) {
            logger.info("local cache hit. {}", cache.get(Arrays.hashCode(bytes)).getUrl());
            return cache.get(Arrays.hashCode(bytes));
        }

        // local cache miss
        String type = imageTypeCheck(new ByteArrayInputStream(bytes));
        if (type == null) {
            return null;
        }

        // save to local
        String fileName = UUID.randomUUID().toString();
        var path = localDir + fileName + "." + type;

        File file = new File(path);
        try {
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        var url = urlPrefix + path;

        return new Image(url, Date.from(Instant.now()), stream.hashCode());
    }

}

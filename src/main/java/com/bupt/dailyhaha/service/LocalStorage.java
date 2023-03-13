package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.Image;
import com.bupt.dailyhaha.Storage;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
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
@Conditional(LocalStorage.class)
@Primary
public class LocalStorage implements Storage, Condition {


    static String localDir = "memes";

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(LocalStorage.class);

    static {
        File file = new File(localDir);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            assert mkdir;
        }
    }

    @Value("${local.urlPrefix}")
    String urlPrefix;

    @Autowired
    CacheService cache;

    @Override
    public Image store(InputStream stream, boolean personal) {
        byte[] bytes = new byte[0];
        try {
            bytes = stream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("read stream failed. {}", stream.hashCode());
        }

        if (cache.contains(Arrays.hashCode(bytes))) {
            logger.info("local cache hit. {}", cache.get(Arrays.hashCode(bytes)).getUrl());
            return cache.get(Arrays.hashCode(bytes));
        }

        // local cache miss
        String type = imageTypeCheck(new ByteArrayInputStream(bytes));
        if (type == null) {
            logger.error("image type check failed. {}", stream.hashCode());
            return null;
        }

        // save to local
        String fileName = UUID.randomUUID() + "." + type;
        var path = localDir + "/" + fileName;

        File file = new File(path);
        try {
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), file);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("save to local failed. {}", path);
            return null;
        }

        var url = urlPrefix + fileName;

        return new Image(url, Date.from(Instant.now()), stream.hashCode(),fileName,false);
    }

    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        var property = env.getProperty("storage.type", String.class, "local");
        return "local".equals(property);
    }
}

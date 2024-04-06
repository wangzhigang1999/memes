package com.bupt.memes.util;

import com.bupt.memes.service.Interface.Storage;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.Callable;

public record AsyncUploader(MultipartFile file, Storage storage) implements Callable<String> {
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(AsyncUploader.class);

    @Override
    public String call() {
        if (file == null) {
            logger.warn("upload image failed, file is null");
            return "";
        }
        try {
            return storage.store(file.getBytes(), file.getContentType()).getUrl();
        } catch (IOException e) {
            logger.error("upload image failed", e);
            return "";
        }
    }
}
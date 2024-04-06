package com.bupt.memes.util;

import com.bupt.memes.model.common.FileUploadResult;
import com.bupt.memes.service.Interface.Storage;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.Callable;

public record FileUploader(MultipartFile file, Storage storage) implements Callable<FileUploadResult> {
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(FileUploader.class);

    @Override
    public FileUploadResult call() {
        if (file == null) {
            logger.warn("upload file failed, file is null");
            return null;
        }
        try {
            return storage.store(file.getBytes(), file.getContentType());
        } catch (IOException e) {
            logger.error("upload file failed", e);
            return null;
        }
    }

    public FileUploadResult upload() {
        return call();
    }
}
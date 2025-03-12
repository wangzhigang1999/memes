package com.memes.util;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.memes.model.common.FileUploadResult;
import com.memes.service.StorageService;

public record FileUploader(MultipartFile file, StorageService storageService) implements Callable<FileUploadResult> {
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(FileUploader.class);

    @Override
    public FileUploadResult call() {
        if (file == null) {
            logger.warn("upload file failed, file is null");
            return null;
        }
        try {
            return storageService.store(file.getBytes(), file.getContentType());
        } catch (IOException e) {
            logger.error("upload file failed", e);
            return null;
        }
    }

    public FileUploadResult upload() {
        return call();
    }
}

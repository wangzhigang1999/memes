package com.bupt.memes.model.common;

public record FileUploadResult(String url, String fileName, String type) {
    public FileUploadResult {
        if (url == null || fileName == null || type == null) {
            throw new IllegalArgumentException("url, fileName, type cannot be null");
        }
    }
}
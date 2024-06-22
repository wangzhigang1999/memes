package com.bupt.memes.service.Interface;

import com.bupt.memes.model.common.FileUploadResult;

import java.util.Map;

public interface Storage {

    default FileUploadResult store(byte[] bytes, String mime) {
        System.out.println("Storage.store() is not implemented");
        return null;
    }

    FileUploadResult store(byte[] bytes, String mime, String path);

    /**
     * 后端的存储可以理解为扁平化的存储，所以删除的时候需要只需要传入 key
     *
     * @param keyList
     *            key 列表
     * @return 删除结果
     */
    default Map<String, Boolean> delete(String[] keyList) {
        System.out.println("Storage.delete() is not implemented");
        return null;
    }

    /**
     * get the extension from mime type
     *
     * @param mime
     *            mime type eg: image/jpeg or jpeg
     * @return extension eg: jpeg
     */
    default String getExtension(String mime) {
        if (mime == null || mime.isEmpty()) {
            throw new IllegalArgumentException("cannot get extension from empty mime type");
        }
        String[] split = mime.split("/");
        return split[split.length - 1].toLowerCase();
    }
}

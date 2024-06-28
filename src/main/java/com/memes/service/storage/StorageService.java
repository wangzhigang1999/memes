package com.memes.service.storage;

import com.memes.exception.AppException;
import com.memes.model.common.FileUploadResult;
import com.memes.util.Preconditions;
import io.micrometer.common.util.StringUtils;
import lombok.SneakyThrows;

import java.util.Map;

public interface StorageService {

    @SneakyThrows
    FileUploadResult store(byte[] bytes, String mime);

    FileUploadResult store(byte[] bytes, String mime, String path);

    /**
     * 后端的存储可以理解为扁平化的存储，所以删除的时候需要只需要传入 key
     *
     * @param keyList
     *            key 列表
     * @return 删除结果
     */
    @SneakyThrows
    Map<String, Boolean> delete(String[] keyList);

    /**
     * get the extension from mime type
     *
     * @param mime
     *            mime type eg: image/jpeg or jpeg
     * @return extension eg: jpeg
     */
    default String getExtension(String mime) {
        Preconditions.checkArgument(StringUtils.isNotBlank(mime), AppException.invalidParam("mime type is null or empty"));
        String[] split = mime.split("/");
        return split[split.length - 1].toLowerCase();
    }
}

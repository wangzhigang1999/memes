package com.memes.service.impl;

import com.memes.model.common.FileUploadResult;
import com.memes.service.StorageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("local")
@Primary
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "local")
@Slf4j
public class LocalStorageServiceImpl implements StorageService {

    static final String localDir = "memes";

    /**
     * 在配置文件中配置的 url 前缀
     * 这个 url 会被拼接到文件名前面然后写入数据库
     */
    @Value("${local.urlPrefix}")
    String urlPrefix;

    static {
        File file = new File(localDir);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            if (!mkdir) {
                log.error("create local dir failed:%s".formatted(localDir));
                System.exit(1);
            }
        }
    }

    @Override
    @SneakyThrows
    public FileUploadResult store(byte[] bytes, String mime) {
        String type = getExtension(mime);
        String fileName = "%d-%s.%s".formatted(System.currentTimeMillis(), UUID.randomUUID(), type);
        var path = "%s/%s".formatted(localDir, fileName);
        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), new File(path));
        var url = urlPrefix + fileName;
        return new FileUploadResult(url, fileName, type);
    }

    @Override
    @SneakyThrows
    public FileUploadResult store(byte[] bytes, String mime, String relativePath) {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        var path = "%s/%s".formatted(localDir, relativePath);
        File file = new File(path);
        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), file);
        return new FileUploadResult(urlPrefix + relativePath, relativePath, getExtension(mime));
    }

    @Override
    public Map<String, Boolean> delete(String[] keyList) {
        Map<String, Boolean> map = new HashMap<>();
        for (String key : keyList) {
            String path = "%s/%s".formatted(localDir, key);
            File file = new File(path);
            boolean delete = file.delete();
            map.put(key, delete);
        }
        return map;
    }
}

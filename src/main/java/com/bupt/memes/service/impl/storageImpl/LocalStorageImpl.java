package com.bupt.memes.service.impl.storageImpl;

import com.bupt.memes.model.common.FileUploadResult;
import com.bupt.memes.service.Interface.Storage;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
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
public class LocalStorageImpl implements Storage {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LocalStorageImpl.class);

    static final String localDir = "memes-img";

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
                throw new RuntimeException("create local dir failed" + localDir);
            } else {
                logger.info("create local dir success");
            }
        }
    }

    @Override
    @SneakyThrows
    public FileUploadResult store(byte[] bytes, String mime) {
        String type = getExtension(mime);
        String fileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + type;
        var path = localDir + "/" + fileName;
        try {
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), new File(path));
        } catch (Exception e) {
            logger.error("store file failed,mime:{}", mime, e);
            return null;
        }
        var url = urlPrefix + fileName;
        return new FileUploadResult(url, fileName, type);
    }

    @Override
    public Map<String, Boolean> delete(String[] keyList) {
        Map<String, Boolean> map = new HashMap<>();
        for (String key : keyList) {
            String path = localDir + "/" + key;
            File file = new File(path);
            boolean delete = file.delete();
            map.put(key, delete);
        }
        return map;
    }
}

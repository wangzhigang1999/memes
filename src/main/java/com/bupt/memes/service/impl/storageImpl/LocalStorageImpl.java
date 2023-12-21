package com.bupt.memes.service.impl.storageImpl;

import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.Storage;
import lombok.SneakyThrows;
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
public class LocalStorageImpl implements Storage {

    static final String localDir = "memes-img";

    static {
        File file = new File(localDir);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            assert mkdir;
        }
    }

    @Value("${local.urlPrefix}")
    String urlPrefix;


    @Override
    @SneakyThrows
    public Submission store(byte[] bytes, String mime) {
        String type = mime.split("/")[1];
        String fileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + type;
        var path = localDir + "/" + fileName;
        try {
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), new File(path));
        } catch (Exception e) {
            return null;
        }
        var url = urlPrefix + fileName;
        Submission submission = new Submission();
        submission.setUrl(url);
        submission.setName(fileName);
        submission.setSubmissionType(mime);
        return submission;
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

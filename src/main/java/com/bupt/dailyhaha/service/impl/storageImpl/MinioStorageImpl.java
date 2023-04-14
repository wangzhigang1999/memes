package com.bupt.dailyhaha.service.impl.storageImpl;

import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.Storage;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Service("minio")
@Conditional(MinioStorageImpl.class)
public class MinioStorageImpl implements Storage, Condition {

    @Value("${minio.bucket}")
    String bucket;

    @Value("${minio.endpoint}")
    String endpoint;

    @Value("${minio.urlPrefix}")
    String urlPrefix;

    @Autowired
    MinioClient client;

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(MinioStorageImpl.class);


    @Override
    public Submission store(byte[] bytes, String mime) {
        String type = mime.split("/")[1];
        String objectName = String.format("%s/%s.%s", "memes", UUID.randomUUID(), type);
        if (putObject(mime, bytes, objectName)) {
            return null;
        }
        Submission submission = new Submission();
        submission.setUrl(urlPrefix + bucket + "/" + objectName);
        submission.setName(objectName);
        submission.setSubmissionType(mime);
        return submission;
    }

    private boolean putObject(String mime, byte[] bytes, String objectName) {
        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .stream(new ByteArrayInputStream(bytes), -1, 10485760)
                .contentType(mime)
                .build();
        try {
            client.putObject(objectArgs);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, Boolean> delete(String[] keyList) {

        List<DeleteObject> objects = new ArrayList<>();
        for (String key : keyList) {
            objects.add(new DeleteObject(key));
        }

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(bucket).objects(objects).build();
        Iterable<Result<DeleteError>> results = client.removeObjects(removeObjectsArgs);
        HashMap<String, Boolean> map = new HashMap<>();

        // 默认都是删除成功的
        for (String key : keyList) {
            map.put(key, true);
        }

        // 如果删除有错，就设置为false
        results.forEach(result -> {
            try {
                DeleteError error = result.get();
                map.put(error.objectName(), false);
                logger.error(error.message());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });

        return map;

    }

    @Override
    public boolean matches(ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        var property = env.getProperty("storage.type", String.class, "local");
        return "minio".equals(property);
    }
}

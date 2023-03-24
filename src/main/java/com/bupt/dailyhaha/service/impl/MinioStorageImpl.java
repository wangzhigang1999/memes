package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.Image;
import com.bupt.dailyhaha.service.Storage;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import static com.bupt.dailyhaha.pojo.Image.imageTypeCheck;

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

    @Autowired
    MongoTemplate mongoTemplate;
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(MinioStorageImpl.class);


    @Override
    public Image store(InputStream stream, boolean personal) {

        byte[] bytes = new byte[0];
        try {
            bytes = stream.readAllBytes();
        } catch (IOException e) {
            logger.error("读取文件失败", e);
        }
        int hashCode = Arrays.hashCode(bytes);

        String type = imageTypeCheck(new ByteArrayInputStream(bytes));
        if (type == null) {
            logger.error("文件类型不支持");
            return null;
        }

        String objectName = String.format("%s/%s.%s", "memes", UUID.randomUUID(), type);
        String contentType = String.format("image/%s", type);


        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .stream(new ByteArrayInputStream(bytes), -1, 10485760)
                .contentType(contentType)
                .build();
        try {
            client.putObject(objectArgs);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

        Image image = new Image();
        image.setUrl(urlPrefix + bucket + "/" + objectName);
        image.setName(objectName);
        image.setHash(hashCode);
        mongoTemplate.save(image);
        return image;
    }

    @Override
    public boolean matches(ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        var property = env.getProperty("storage.type", String.class, "minio");
        return "minio".equals(property);
    }
}

package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.submission.Image;
import com.bupt.dailyhaha.pojo.submission.Submission;
import com.bupt.dailyhaha.service.Storage;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import static com.bupt.dailyhaha.pojo.submission.Image.imageTypeCheck;

@Service("local")
@Conditional(LocalStorageImpl.class)
@Primary
public class LocalStorageImpl implements Storage, Condition {


    static String localDir = "memes";

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(LocalStorageImpl.class);

    static {
        File file = new File(localDir);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            assert mkdir;
        }
    }

    @Value("${local.urlPrefix}")
    String urlPrefix;

    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public Image store(InputStream stream, boolean personal) {
        byte[] bytes = new byte[0];
        int hashCode = Arrays.hashCode(bytes);
        try {
            bytes = stream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("read stream failed. {}", hashCode);
        }

        String type = imageTypeCheck(new ByteArrayInputStream(bytes));
        if (type == null) {
            logger.error("image type check failed. {}", hashCode);
            return null;
        }

        // save to local
        String fileName = UUID.randomUUID() + "." + type;
        var path = localDir + "/" + fileName;

        File file = new File(path);
        try {
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), file);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("save to local failed. {}", path);
            return null;
        }

        var url = urlPrefix + fileName;
        Image image = new Image(url, fileName, hashCode);
        if (!personal) {
            mongoTemplate.save(image);
        }
        return image;
    }


    @Override
    public Submission store(InputStream stream, String mime, boolean personal) {
        byte[] bytes = Utils.readAllBytes(stream);
        if (bytes == null) {
            return null;
        }

        int code = Arrays.hashCode(bytes);
        String type = mime.split("/")[1];

        // save to local
        String fileName = UUID.randomUUID() + "." + type;
        var path = localDir + "/" + fileName;

        boolean saved = Utils.saveFile(bytes, path);
        if (!saved) {
            return null;
        }

        var url = urlPrefix + fileName;
        Submission submission = new Submission();
        submission.setUrl(url);
        submission.setName(fileName);
        submission.setHash(code);
        submission.setSubmissionType(mime);

        if (!personal) {
            mongoTemplate.save(submission);
        }
        return submission;
    }


    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        var property = env.getProperty("storage.type", String.class, "local");
        return "local".equals(property);
    }
}

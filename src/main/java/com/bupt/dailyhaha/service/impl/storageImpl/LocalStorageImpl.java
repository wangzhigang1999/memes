package com.bupt.dailyhaha.service.impl.storageImpl;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.service.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;


@Service("local")
@Conditional(LocalStorageImpl.class)
@Primary
public class LocalStorageImpl implements Storage, Condition {


    static String localDir = "memes";

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
    public Submission store(byte[] bytes, String mime) {
        String type = mime.split("/")[1];
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
        submission.setSubmissionType(mime);
        return submission;
    }


    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        var property = env.getProperty("storage.type", String.class, "local");
        return "local".equals(property);
    }
}

package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.IStorage;
import com.bupt.dailyhaha.util.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;


@Service("local")
public class LocalIStorageImpl implements IStorage {


    static String localDir = "memes";

    static {
        File file = new File(localDir);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            assert mkdir;
        }
    }

    @Value("${urlPrefix}")
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

}

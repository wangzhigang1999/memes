package com.bupt.memes.service.impl.storageImpl;

import com.bupt.memes.pojo.media.Submission;
import com.bupt.memes.service.Interface.Storage;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;


@Service("qiniu")
@Conditional(QiNiuStorageImpl.class)
public class QiNiuStorageImpl implements Storage, Condition {
    @Value("${qiniu.accessKey}")
    String accessKey;
    @Value("${qiniu.secretKey}")
    String secretKey;
    @Value("${qiniu.bucket}")
    String bucket;
    @Value("${qiniu.urlPrefix}")
    String urlPrefix;


    static final UploadManager manager;

    static {
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        manager = new UploadManager(cfg);
    }


    /**
     * 上传图片到七牛云
     *
     * @param bytes 图片字节数组
     * @param ext   图片后缀/扩展名
     * @return 七牛云上的图片名=文件路径+文件名
     */
    @SneakyThrows
    private String putImg(byte[] bytes, String ext) {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        String uuid = UUID.randomUUID().toString();
        long timeMillis = System.currentTimeMillis();
        var fileName = "shadiao/".concat(String.valueOf(timeMillis)).concat("-").concat(uuid).concat(".").concat(ext);
        Response response = manager.put(bytes, fileName, upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }


    @Override
    @SneakyThrows
    public Submission store(byte[] bytes, String mime) {
        String type = mime.split("/")[1];
        String fileName = putImg(bytes, type.toLowerCase());

        var url = urlPrefix.concat(fileName);
        Submission submission = new Submission();
        submission.setUrl(url);
        submission.setName(fileName);
        submission.setSubmissionType(mime);
        return submission;
    }

    @Override
    @SneakyThrows
    public HashMap<String, Boolean> delete(String[] keyList) {
        if (keyList == null || keyList.length == 0) {
            return null;
        }
        Auth auth = Auth.create(accessKey, secretKey);
        Configuration cfg = new Configuration(Region.autoRegion());
        BucketManager bucketManager = new BucketManager(auth, cfg);

        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(bucket, keyList);

        Response response = bucketManager.batch(batchOperations);
        BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
        var nameStatusMap = new HashMap<String, Boolean>();
        for (int i = 0; i < keyList.length; i++) {
            BatchStatus status = batchStatusList[i];
            String key = keyList[i];
            nameStatusMap.put(key, status.code == 200);
        }
        return nameStatusMap;

    }

    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        var property = env.getProperty("storage.type", String.class, "local");
        return "qiniu".equals(property);
    }
}

package com.bupt.memes.service.impl.storageImpl;

import com.bupt.memes.model.common.FileUploadResult;
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
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service("qiniu")
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "qiniu")
public class QiNiuStorageImpl implements Storage {
    @Value("${qiniu.accessKey}")
    String accessKey;
    @Value("${qiniu.secretKey}")
    String secretKey;
    @Value("${qiniu.bucket}")
    String bucket;
    @Value("${qiniu.urlPrefix}")
    String urlPrefix;

    @Value("${qiniu.dirName}")
    String ossDirName = "shadiao";

    static final UploadManager manager;

    final static Gson gson = new Gson();

    static {
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        manager = new UploadManager(cfg);
    }

    @PostConstruct
    void init() {
        if (!ossDirName.endsWith("/")) {
            ossDirName = ossDirName.concat("/");
        }
    }

    @Override
    @SneakyThrows
    public FileUploadResult store(byte[] bytes, String mime) {
        String type = getExtension(mime);
        String fileName = putImg(bytes, type);
        if (fileName == null) {
            return null;
        }
        var url = urlPrefix.concat(fileName);
        return new FileUploadResult(url, fileName, mime);
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

    /**
     * 上传图片到七牛云
     *
     * @param bytes
     *            图片字节数组
     * @param ext
     *            图片后缀/扩展名
     * @return 七牛云上的图片名 =文件路径 + 文件名
     */
    @SneakyThrows
    private String putImg(byte[] bytes, String ext) {
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(bucket);
        String uuid = UUID.randomUUID().toString();
        long timeMillis = System.currentTimeMillis();
        // timestamp-uuid.ext, time is used to sort the images
        var fileName = ossDirName.concat(String.valueOf(timeMillis)).concat("-").concat(uuid).concat(".").concat(ext);
        Response response = manager.put(bytes, fileName, uploadToken);
        DefaultPutRet putRet = gson.fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }
}

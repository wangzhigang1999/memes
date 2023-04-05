package com.bupt.dailyhaha.service.impl.storageImpl;

import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.service.Storage;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

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

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(QiNiuStorageImpl.class);


    static UploadManager manager;

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
     * @throws QiniuException 上传失败
     */
    private String putImg(byte[] bytes, String ext) throws QiniuException {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        String uuid = UUID.randomUUID().toString();
        Response response = manager.put(bytes, "shadiao/".concat(uuid).concat(".").concat(ext), upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }


    @Override
    public Submission store(byte[] bytes, String mime) {
        String type = mime.split("/")[1];
        String fileName;
        try {
            fileName = putImg(bytes, type.toLowerCase());
        } catch (QiniuException e) {
            logger.error("put image error", e);
            return null;
        }
        var url = urlPrefix.concat(fileName);
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
        return "qiniu".equals(property);
    }
}
package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.Image;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static com.bupt.dailyhaha.pojo.Image.imageTypeCheck;

@Service("qiniu")
@Conditional(QiNiuOssStorage.class)
public class QiNiuOssStorage implements Storage, Condition {
    @Value("${qiniu.accessKey}")
    String accessKey;
    @Value("${qiniu.secretKey}")
    String secretKey;
    @Value("${qiniu.bucket}")
    String bucket;

    @Value("${qiniu.urlPrefix}")
    String urlPrefix;


    @Autowired
    MongoTemplate mongoTemplate;
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(QiNiuOssStorage.class);
    @Autowired
    CacheService cache;


    static UploadManager manager;

    static {
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        manager = new UploadManager(cfg);
    }


    /**
     * 上传图片
     *
     * @param stream   图片流
     * @param personal 是否是个人投稿
     * @return 图片信息
     * @throws IOException 上传失败
     */
    private Image putImg(InputStream stream, boolean personal) throws IOException {

        byte[] bytes = stream.readAllBytes();

        // local cache hit
        if (cache.contains(Arrays.hashCode(bytes))) {
            logger.info("local cache hit. {}", cache.get(Arrays.hashCode(bytes)).getUrl());
            return cache.get(Arrays.hashCode(bytes));
        }

        logger.info("local cache miss. {}", Arrays.hashCode(bytes));

        // local cache miss
        String type = imageTypeCheck(new ByteArrayInputStream(bytes));
        if (type == null) {
            return null;
        }
        var fileName = putImg(bytes, type.toLowerCase());
        var url = urlPrefix.concat(fileName);

        Image image = new Image();
        image.setUrl(url);
        image.setTime(Date.from(java.time.Instant.now()));
        image.setHash(Arrays.hashCode(bytes));
        image.setName(fileName);

        // put into local cache
        cache.put(image);

        // 如果是投稿，就存入数据库
        if (!personal) {
            mongoTemplate.save(image);
        }

        return image;
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
    public Image store(InputStream stream, boolean personal) {
        try {
            return putImg(stream, personal);
        } catch (Exception e) {
            logger.error("put image error", e);
            return null;
        }
    }

    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        var property = env.getProperty("storage.type", String.class, "local");
        logger.info("storage type: {}", property);
        return "qiniu".equals(property);
    }
}

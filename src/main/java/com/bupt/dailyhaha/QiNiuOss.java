package com.bupt.dailyhaha;

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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QiNiuOss {
    @Value("${qiniu.accessKey}")
    String accessKey;
    @Value("${qiniu.secretKey}")
    String secretKey;
    @Value("${qiniu.bucket}")
    String bucket;

    @Value("${qiniu.urlPrefix}")
    String urlPrefix;

    final MongoTemplate mongoTemplate;

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(QiNiuOss.class);


    static UploadManager manager;

    static Map<Integer, Image> map = new ConcurrentHashMap<>();

    static {
        Configuration cfg = new Configuration(Region.region1());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        manager = new UploadManager(cfg);
    }

    public QiNiuOss(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;

        // init local cache
        mongoTemplate.findAll(Image.class).forEach(image -> map.put(image.getHash(), image));
        logger.info("init local cache. size: {}", map.size());
    }


    public static String imageTypeCheck(InputStream stream) {
        try {
            ImageInputStream image = ImageIO.createImageInputStream(stream);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(image);
            return readers.next().getFormatName();

        } catch (Exception e) {
            return null;
        }

    }

    public Image putImg(InputStream stream, boolean personal) throws IOException {

        byte[] bytes = stream.readAllBytes();

        // local cache hit
        if (map.containsKey(Arrays.hashCode(bytes))) {
            logger.info("local cache hit. {}", map.get(Arrays.hashCode(bytes)).getUrl());
            return map.get(Arrays.hashCode(bytes));
        }

        // local cache miss
        String type = imageTypeCheck(new ByteArrayInputStream(bytes));
        if (type == null) {
            return null;
        }
        var url = putImg(bytes, type.toLowerCase());

        Image image = new Image();
        image.setUrl(url);
        image.setTime(Date.from(java.time.Instant.now()));
        image.setHash(Arrays.hashCode(bytes));

        // put into local cache
        map.put(Arrays.hashCode(bytes), image);

        // 如果是投稿，就存入数据库
        if (!personal) {
            mongoTemplate.save(image);
        }

        return image;
    }

    private String putImg(byte[] bytes, String ext) throws QiniuException {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        String uuid = UUID.randomUUID().toString();
        Response response = manager.put(bytes, "shadiao/".concat(uuid).concat(".").concat(ext), upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return urlPrefix.concat(putRet.key);
    }

}

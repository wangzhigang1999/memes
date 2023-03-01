package com.bupt.dailyhaha;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;

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


    static UploadManager manager;

    static {
        Configuration cfg = new Configuration(Region.region1());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        manager = new UploadManager(cfg);
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

    public String putImg(InputStream stream) throws IOException {

        byte[] bytes = stream.readAllBytes();

        String type = imageTypeCheck(new ByteArrayInputStream(bytes));
        if (type == null) {
            return null;
        }
        return putImg(bytes, type.toLowerCase());

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

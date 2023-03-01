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
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region1());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        manager = new UploadManager(cfg);
    }


    public String putImg(String localFilePath) throws QiniuException {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        String uuid = UUID.randomUUID().toString();
        Response response = manager.put(localFilePath, "shadiao/".concat(uuid), upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return urlPrefix.concat(putRet.key);
    }

    public String putImg(byte[] bytes) throws QiniuException {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        String uuid = UUID.randomUUID().toString();
        Response response = manager.put(bytes, "shadiao/".concat(uuid), upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return urlPrefix.concat(putRet.key);
    }

    public static void main(String[] args) throws QiniuException {
        QiNiuOss qiNiuOss = new QiNiuOss();
        String key = qiNiuOss.putImg("D:\\codes\\dailyHaha\\FoE6an2XgAI5_dA.jpg");
        System.out.println(key);
    }
}

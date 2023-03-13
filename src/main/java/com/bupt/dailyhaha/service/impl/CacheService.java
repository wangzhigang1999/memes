package com.bupt.dailyhaha.service.impl;

import com.bupt.dailyhaha.pojo.Image;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(CacheService.class);
    static Map<Integer, Image> map = new ConcurrentHashMap<>();
    final MongoTemplate mongoTemplate;


    public CacheService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        // 很简单的逻辑,就是把数据库里的数据全部读出来，放到内存里
        // 不考虑哈希冲突的问题
        mongoTemplate.findAll(Image.class).forEach(image -> map.put(image.getHash(), image));
        logger.info("init local cache. size: {}", map.size());
    }

    public Image get(Integer hash) {
        return map.get(hash);
    }

    public void put(Image image) {
        map.put(image.getHash(), image);
    }

    public boolean contains(Integer hash) {
        return map.containsKey(hash);
    }
}

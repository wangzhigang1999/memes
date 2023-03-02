package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.Image;
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
        // init local cache
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

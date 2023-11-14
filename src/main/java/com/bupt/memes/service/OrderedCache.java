package com.bupt.memes.service;

import com.bupt.memes.service.Interface.IndexMapKey;
import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class OrderedCache<T extends IndexMapKey> {
    private final TreeMap<String, T> map = new TreeMap<>(Comparator.reverseOrder());
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    // max size
    private final Integer maxSize;

    private final static Integer DEFAULT_EVICTION_SIZE = 2;


    public OrderedCache() {
        this.maxSize = Integer.MAX_VALUE;
    }


    // 添加元素
    public void add(T value) {
        // if size is larger than max size, then evict
        if (map.size() >= maxSize) {
            evict();
        }
        writeLock.lock();
        map.put(value.getIndexMapKey(), value);
        writeLock.unlock();
    }

    public void replace(T value) {
        writeLock.lock();
        map.replace(value.getIndexMapKey(), value);
        writeLock.unlock();
    }


    @Timed(value = "cache_may_hit", description = "cache may hit")
    public List<T> getAfter(String key, Integer limit) {
        readLock.lock();
        SortedMap<String, T> tailMap = map.tailMap(key, false);
        List<T> limitedValues = getLimitedValues(tailMap, limit);
        readLock.unlock();
        return limitedValues;
    }

    // 辅助方法，用于获取有限数量的值
    private List<T> getLimitedValues(SortedMap<String, T> subMap, Integer limit) {
        List<T> result = new ArrayList<>();
        for (T value : subMap.values()) {
            result.add(value);
            if (limit != null && result.size() >= limit) {
                break;
            }
        }
        return result;
    }

    public void putAll(List<T> list) {
        if (list.size() > maxSize) {
            throw new IllegalArgumentException("list size is larger than max size");
        }
        writeLock.lock();
        for (T t : list) {
            add(t);
        }
        writeLock.unlock();
    }

    private void evict() {
        writeLock.lock();
        for (int i = 0; i < DEFAULT_EVICTION_SIZE; i++) {
            if (!map.isEmpty()) {
                map.remove(map.firstKey());
            }
        }
        writeLock.unlock();
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

    public int size() {
        return this.map.size();
    }

    public Boolean clear() {
        writeLock.lock();
        this.map.clear();
        writeLock.unlock();
        return true;
    }
}















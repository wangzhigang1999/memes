package com.bupt.memes.model;

import com.bupt.memes.service.Interface.IndexMapKey;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class IndexMap<T extends IndexMapKey> {
    private final TreeMap<String, T> map = new TreeMap<>(Comparator.reverseOrder());
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // max size
    private final Integer maxSize;

    private final static Integer DEFAULT_EVICTION_SIZE = 2;


    public IndexMap() {
        this.maxSize = Integer.MAX_VALUE;
    }


    // 添加元素
    public void add(T value) {
        // if size is larger than max size, then evict
        if (map.size() >= maxSize) {
            evict();
        }
        readWriteLock.writeLock().lock();
        map.put(value.getIndexMapKey(), value);
        readWriteLock.writeLock().unlock();
    }

    public void replace(T value) {
        readWriteLock.writeLock().lock();
        map.replace(value.getIndexMapKey(), value);
        readWriteLock.writeLock().unlock();
    }


    // get after
    public List<T> getAfter(String key, Integer limit) {
        readWriteLock.readLock().lock();
        SortedMap<String, T> tailMap = map.tailMap(key, false);
        List<T> limitedValues = getLimitedValues(tailMap, limit);
        readWriteLock.readLock().unlock();
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
        readWriteLock.writeLock().lock();
        for (T t : list) {
            add(t);
        }
        readWriteLock.writeLock().unlock();
    }

    private void evict() {
        readWriteLock.writeLock().lock();
        for (int i = 0; i < DEFAULT_EVICTION_SIZE; i++) {
            if (!map.isEmpty()) {
                map.remove(map.firstKey());
            }
        }
        readWriteLock.writeLock().unlock();
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

    public int size() {
        return this.map.size();
    }
}















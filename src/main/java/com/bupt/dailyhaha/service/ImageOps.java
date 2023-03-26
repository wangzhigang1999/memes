package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.submission.Image;

import java.util.List;

public interface ImageOps {
    boolean deleteByName(String name);

    List<Image> getToday();

    void addDeleteTask(String name);

    boolean vote(String name, boolean up);

    /**
     * 获取历史记录的最后一条
     */
    List<Image> getLastHistory();

    /**
     * 更新历史记录
     */
    boolean updateHistory(String date, List<Image> images);
}

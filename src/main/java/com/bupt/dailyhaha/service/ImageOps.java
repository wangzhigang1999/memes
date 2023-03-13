package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Image;

import java.util.List;

public interface ImageOps {
    boolean deleteByName(String name);

    List<Image> getToday();

    void addDeleteTask(String name);
}

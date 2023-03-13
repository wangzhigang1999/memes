package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Image;

import java.util.List;

public interface ImageOps {
    void deleteByName(String name);

    List<Image> getToday();
}

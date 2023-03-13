package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Image;

import java.io.InputStream;

public interface Storage {
    Image store(InputStream stream, boolean personal);
}

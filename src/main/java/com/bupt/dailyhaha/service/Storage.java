package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Submission;

public interface Storage {

    default Submission store(byte[] bytes, String mime) {
        System.out.println("Storage.store() is not implemented");
        return null;
    }
}

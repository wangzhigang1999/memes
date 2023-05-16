package com.bupt.dailyhaha.service.Interface;

import com.bupt.dailyhaha.pojo.media.Submission;

import java.util.HashMap;

public interface Storage {

    default Submission store(byte[] bytes, String mime) {
        System.out.println("Storage.store() is not implemented");
        return null;
    }

    default HashMap<String, Boolean> delete(String[] keyList) {
        System.out.println("Storage.delete() is not implemented");
        return null;
    }
}

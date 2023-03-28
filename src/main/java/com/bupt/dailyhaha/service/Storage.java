package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.Submission;

import java.io.InputStream;

public interface Storage {

    default Submission store(InputStream stream, String mime, boolean personal) {
        System.out.println("Storage.store() is not implemented");
        return null;
    }
}

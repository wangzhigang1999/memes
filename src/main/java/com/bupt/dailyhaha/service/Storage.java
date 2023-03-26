package com.bupt.dailyhaha.service;

import com.bupt.dailyhaha.pojo.submission.Image;
import com.bupt.dailyhaha.pojo.submission.Submission;

import java.io.InputStream;

public interface Storage {
    Image store(InputStream stream, boolean personal);

    default Submission store(InputStream stream, String mime, boolean personal) {
        System.out.println("Storage.store() is not implemented");
        return null;
    }
}

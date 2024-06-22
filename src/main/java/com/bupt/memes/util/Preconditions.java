package com.bupt.memes.util;

import com.bupt.memes.exception.AppException;

public class Preconditions {
    public static void checkArgument(boolean condition, AppException exception) {
        if (!condition) {
            throw exception;
        }
    }

    public static void checkNotNull(Object o, AppException e) {
        if (o == null) {
            throw e;
        }
    }

    public static void checkStringNotEmpty(String str, AppException e) {
        if (str == null || str.isEmpty()) {
            throw e;
        }
    }
}

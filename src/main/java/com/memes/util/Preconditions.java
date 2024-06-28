package com.memes.util;

import com.memes.exception.AppException;

import java.util.function.Supplier;

public class Preconditions {
    public static void checkArgument(boolean condition, AppException exception) {
        if (!condition) {
            throw exception;
        }
    }

    public static void checkArgument(boolean condition, Supplier<AppException> e) {
        if (!condition) {
            throw e.get();
        }
    }

    public static void checkNotNull(Object o, AppException e) {
        if (o == null) {
            throw e;
        }
    }

    public static void checkNotNull(Object o, Supplier<AppException> e) {
        if (o == null) {
            throw e.get();
        }
    }

    public static void checkStringNotEmpty(String str, AppException e) {
        if (str == null || str.isEmpty()) {
            throw e;
        }
    }

    public static void checkStringNotEmpty(String str, Supplier<AppException> e) {
        if (str == null || str.isEmpty()) {
            throw e.get();
        }
    }
}

package com.memes.util;

import java.util.HashSet;
import java.util.Set;

public class ResourceChecker {

    private static final Set<String> STATIC_EXTENSIONS = new HashSet<>();

    static {
        // 初始化静态资源扩展名集合
        STATIC_EXTENSIONS.add(".html");
        STATIC_EXTENSIONS.add(".css");
        STATIC_EXTENSIONS.add(".js");
        STATIC_EXTENSIONS.add(".jpg");
        STATIC_EXTENSIONS.add(".jpeg");
        STATIC_EXTENSIONS.add(".png");
        STATIC_EXTENSIONS.add(".gif");
        STATIC_EXTENSIONS.add(".ico");
        STATIC_EXTENSIONS.add(".svg");
    }

    public static boolean isStaticResource(String url) {
        url = url.toLowerCase();
        int lastDotIndex = url.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return false; // 没有扩展名
        }
        String extension = url.substring(lastDotIndex);
        return STATIC_EXTENSIONS.contains(extension);
    }
}

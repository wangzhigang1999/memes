package com.memes.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class FileReaderUtil {

    /**
     * 读取 resources 目录中的文本文件并返回字符串
     *
     * @param fileName
     *            文件名（相对于 resources 目录）
     * @return 文件内容字符串
     * @throws IOException
     *             如果读取文件失败
     */
    public static String readFileAsString(String fileName) throws IOException {
        try (InputStream inputStream = getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * 读取 resources 目录中的文本文件并返回按行分割的列表
     *
     * @param fileName
     *            文件名（相对于 resources 目录）
     * @return 文件内容的每一行组成的列表
     * @throws IOException
     *             如果读取文件失败
     */
    public static List<String> readFileAsList(String fileName) throws IOException {
        try (InputStream inputStream = getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    /**
     * 获取资源文件的输入流
     *
     * @param fileName
     *            文件名（相对于 resources 目录）
     * @return InputStream 文件输入流
     * @throws IOException
     *             如果文件不存在
     */
    private static InputStream getResourceAsStream(String fileName) throws IOException {
        ClassLoader classLoader = FileReaderUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IOException("File not found: %s".formatted(fileName));
        }
        return inputStream;
    }
}

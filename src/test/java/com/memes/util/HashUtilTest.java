package com.memes.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

public class HashUtilTest {

    @Test
    public void testStrToHex() {
        // 测试字符串 "hello" 的 MD5 和 SHA-256 哈希值
        String input = "hello";
        String expectedMd5 = "5d41402abc4b2a76b9719d911017c592"; // hello 的 MD5 哈希值
        String expectedSha256 = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"; // hello 的 SHA-256 哈希值

        assertEquals(expectedMd5, HashUtil.strToHex(input, HashUtil.HashAlgorithm.MD5));
        assertEquals(expectedSha256, HashUtil.strToHex(input, HashUtil.HashAlgorithm.SHA_256));
    }

    @Test
    public void testFileToHex() throws Exception {
        // 准备一个测试文件（假设 resources 目录下有一个名为 test.txt 的文件）
        File file = ResourceUtils.getFile("classpath:test.txt");
        String fileContent = "test content"; // 文件内容
        byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);

        // 计算文件内容的 MD5 和 SHA-256 哈希值
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");

        String expectedMd5 = helper(md5Digest.digest(fileBytes));
        String expectedSha256 = helper(sha256Digest.digest(fileBytes));

        // 验证 fileToHex 方法的结果
        assertEquals(expectedMd5, HashUtil.fileToHex(file, HashUtil.HashAlgorithm.MD5));
        assertEquals(expectedSha256, HashUtil.fileToHex(file, HashUtil.HashAlgorithm.SHA_256));
    }

    private String helper(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

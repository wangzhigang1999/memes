package com.memes.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import lombok.Getter;
import lombok.SneakyThrows;

public class HashUtil {
    @Getter
    public enum HashAlgorithm {
        MD5("MD5"), SHA_256("SHA-256");

        private final String algorithm;

        HashAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

    }

    @SneakyThrows
    public static String fileToHex(File file, HashAlgorithm algorithm) {
        MessageDigest digest = MessageDigest.getInstance(algorithm.algorithm);
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[8192]; // 8KB 缓冲区
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        fis.close();
        return helper(digest.digest());
    }

    // bytes to hex
    @SneakyThrows
    public static String bytesToHex(byte[] bytes, HashAlgorithm algorithm) {
        MessageDigest md = MessageDigest.getInstance(algorithm.algorithm);
        byte[] digest = md.digest(bytes);
        return helper(digest);
    }

    @SneakyThrows
    public static String strToHex(String input, HashAlgorithm algorithm) {
        MessageDigest md = MessageDigest.getInstance(algorithm.algorithm);
        byte[] digest = md.digest(input.getBytes());
        return helper(digest);
    }

    private static String helper(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}

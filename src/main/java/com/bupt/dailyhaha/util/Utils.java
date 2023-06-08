package com.bupt.dailyhaha.util;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class Utils {

    /**
     * get today start unix epoch milli
     *
     * @return today start unix epoch milli
     */
    public static long getTodayStartUnixEpochMilli() {
        var now = Instant.now();
        var today = now.atZone(java.time.ZoneId.of("Asia/Shanghai")).toLocalDate();
        return today.atStartOfDay(java.time.ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
    }

    /**
     * get today in the format of YYYY-MM-DD
     *
     * @return today in the format of YYYY-MM-DD
     */
    public static String getYMD() {
        var now = Instant.now();
        var today = now.atZone(java.time.ZoneId.of("Asia/Shanghai")).toLocalDate();
        return today.toString();
    }

    /**
     * get start epoch milli of a date
     *
     * @param date date in the format of YYYY-MM-DD
     * @return start epoch milli of a date
     */
    public static Long getStartEpochMilli(String date) {
        return LocalDate.parse(date).atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
    }


    /**
     * read all bytes from input stream
     *
     * @param stream input stream
     * @return byte array
     */
    public static byte[] readAllBytes(InputStream stream) {
        try {
            return stream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * save bytes to file
     *
     * @param bytes bytes
     * @param path  path
     * @return true if saved successfully
     */
    public static boolean saveFile(byte[] bytes, String path) {
        try {
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), new File(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * get current hour in Asia/Shanghai
     *
     * @return current hour in Asia/Shanghai
     */
    public static int getCurrentHour() {
        var now = Instant.now();
        return now.atZone(java.time.ZoneId.of("Asia/Shanghai")).getHour();
    }

    public static void main(String[] args) {
        System.out.println(getTodayStartUnixEpochMilli());
        System.out.println(getYMD());

        long l = getTodayStartUnixEpochMilli() - 2 * 60 * 60 * 1000;
        System.out.println(l);
        // convert l to  time format YYYY-MM-DD HH:mm:ss
        var str = Instant.ofEpochMilli(l).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();
        System.out.println(str);


        String date = "2023-05-24";
        System.out.println(LocalDate.parse(date).atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli());


        String s = Instant.parse("2023-01-01T00:00:00.00Z").atZone(ZoneId.of("Asia/Shanghai")).toLocalDate().toString();
        System.out.println(s);


        var start = Instant.parse("2023-01-01T00:00:00.00Z").atZone(ZoneId.of("Asia/Shanghai")).toLocalDate();
        var today = Instant.now().atZone(java.time.ZoneId.of("Asia/Shanghai")).toLocalDate();
        int to = (int) (today.toEpochDay() - start.toEpochDay());
        System.out.println(to);
    }
}

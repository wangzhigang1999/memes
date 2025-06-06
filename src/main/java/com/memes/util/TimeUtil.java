package com.memes.util;

import java.time.Instant;
import java.time.LocalDateTime;

public class TimeUtil {

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
     * get current hour in Asia/Shanghai
     *
     * @return current hour in Asia/Shanghai
     */
    public static int getCurrentHour() {
        var now = Instant.now();
        return now.atZone(java.time.ZoneId.of("Asia/Shanghai")).getHour();
    }

    // convert YYYY-MM-DD to unix epoch milli
    public static long convertYMDToUnixEpochMilli(String ymd) {
        var localDate = java.time.LocalDate.parse(ymd);
        return localDate.atStartOfDay(java.time.ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
    }

    public static LocalDateTime convertYMDToLocalDateTime(String date) {
        long epochMilli = convertYMDToUnixEpochMilli(date);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), java.time.ZoneId.of("Asia/Shanghai"));
    }
}

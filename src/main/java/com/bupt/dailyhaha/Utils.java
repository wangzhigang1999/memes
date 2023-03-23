package com.bupt.dailyhaha;

import java.time.Instant;
import java.util.Map;

import static com.bupt.dailyhaha.Audit.start;

public class Utils {

    /**
     * get uptime
     *
     * @param duration duration in milli
     * @return map of status, msg, start at
     */
    public static Map<String, Object> up(long duration) {
        // convert start to yyyy-MM-dd HH:mm:ss with beijing time zone
        var str = Instant.ofEpochMilli(start).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();

        //convert duration to hours
        var hours = duration / 1000 / 60 / 60;
        if (hours > 24) {
            return Map.of("status", "ok", "msg", "up " + hours / 24.0 + " days", "start at:", str);
        } else if (hours > 1) {
            return Map.of("status", "ok", "msg", "up " + hours + " hours", "start at:", str);
        }

        // convert duration to minutes
        var minutes = duration / 1000 / 60;
        if (minutes > 1) {
            return Map.of("status", "ok", "msg", "up " + minutes + " minutes", "start at:", str);
        } else {
            return Map.of("status", "ok", "msg", "up " + duration / 1000 + " seconds", "start at:", str);
        }
    }

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
     * @return today in the format of YYYY-MM-DD
     */
    public static String getYMD() {
        var now = Instant.now();
        var today = now.atZone(java.time.ZoneId.of("Asia/Shanghai")).toLocalDate();
        return today.toString();
    }

    public static void main(String[] args) {
        System.out.println(getTodayStartUnixEpochMilli());
        System.out.println(getYMD());

        long l = getTodayStartUnixEpochMilli() - 2 * 60 * 60 * 1000;
        System.out.println(l);
        // convert l to  time format YYYY-MM-DD HH:mm:ss
        var str = Instant.ofEpochMilli(l).atZone(java.time.ZoneId.of("Asia/Shanghai")).toString();
        System.out.println(str);
    }
}

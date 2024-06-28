package com.memes.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilTest {

    @Test
    void getTodayStartUnixEpochMilliReturnsCorrectValue() {
        long startOfToday = Instant.now().atZone(ZoneId.of("Asia/Shanghai")).toLocalDate().atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        assertEquals(startOfToday, TimeUtil.getTodayStartUnixEpochMilli());
    }

    @Test
    void getYMDReturnsCorrectValue() {
        String today = Instant.now().atZone(ZoneId.of("Asia/Shanghai")).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        assertEquals(today, TimeUtil.getYMD());
    }

    @Test
    void getCurrentHourReturnsCorrectValue() {
        int currentHour = Instant.now().atZone(ZoneId.of("Asia/Shanghai")).getHour();
        assertEquals(currentHour, TimeUtil.getCurrentHour());
    }

    @Test
    void convertYMDToUnixEpochMilliReturnsCorrectValue() {
        String ymd = "2022-01-01";
        long expected = java.time.LocalDate.parse(ymd).atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        assertEquals(expected, TimeUtil.convertYMDToUnixEpochMilli(ymd));
    }

    @Test
    void convertYMDToUnixEpochMilliThrowsExceptionForInvalidFormat() {
        String ymd = "invalid-format";
        assertThrows(DateTimeParseException.class, () -> TimeUtil.convertYMDToUnixEpochMilli(ymd));
    }
}
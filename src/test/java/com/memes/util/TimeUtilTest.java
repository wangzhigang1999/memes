package com.memes.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimeUtilTest {

    private ZoneId shanghaiZone;

    @BeforeEach
    public void setUp() {
        shanghaiZone = ZoneId.of("Asia/Shanghai");
    }

    @Test
    public void getTodayStartUnixEpochMilli_ShouldReturnStartOfToday() {
        long expected = ZonedDateTime
            .now(shanghaiZone)
            .toLocalDate()
            .atStartOfDay(shanghaiZone)
            .toInstant()
            .toEpochMilli();
        long actual = TimeUtil.getTodayStartUnixEpochMilli();
        assertEquals(expected, actual);
    }

    @Test
    public void getYMD_ShouldReturnCorrectFormat() {
        String expected = ZonedDateTime
            .now(shanghaiZone)
            .toLocalDate()
            .toString();
        String actual = TimeUtil.getYMD();
        assertEquals(expected, actual);
    }

    @Test
    public void getCurrentHour_ShouldReturnCorrectHour() {
        int expected = ZonedDateTime.now(shanghaiZone).getHour();
        int actual = TimeUtil.getCurrentHour();
        assertEquals(expected, actual);
    }

    @Test
    public void convertYMDToUnixEpochMilli_ShouldReturnCorrectEpochMilli() {
        String ymd = "2023-10-01";
        long expected = LocalDate
            .parse(ymd)
            .atStartOfDay(shanghaiZone)
            .toInstant()
            .toEpochMilli();
        long actual = TimeUtil.convertYMDToUnixEpochMilli(ymd);
        assertEquals(expected, actual);
    }
}

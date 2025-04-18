package com.memes.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class GsonUtilTest {
    @Getter
    @AllArgsConstructor
    @Data
    private static class TestObject {
        private final String name;
        private final Object value;
    }

    @Test
    void testToJson() {
        // Test with simple object
        TestObject obj = new TestObject("test", 123);
        String json = GsonUtil.toJson(obj);
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"value\":123"));

        // Test with null object
        String nullJson = GsonUtil.toJson(null);
        assertEquals("null", nullJson);
    }

    @Test
    void testFromJson() {
        // Test with simple object
        String json = "{\"name\":\"test\",\"value\":123}";
        TestObject obj = GsonUtil.fromJson(json, TestObject.class);
        assertNotNull(obj);
        assertEquals("test", obj.getName());
        assertEquals(123.0, obj.getValue());

        // Test with invalid JSON
        String invalidJson = "invalid json";
        TestObject nullObj = GsonUtil.fromJson(invalidJson, TestObject.class);
        assertNull(nullObj);
    }

    @Test
    void testLocalDateTimeSerialization() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 30, 45);
        String json = GsonUtil.toJson(dateTime);
        assertNotNull(json);
        assertTrue(json.contains("2024-01-01T12:30:45"));

        LocalDateTime deserialized = GsonUtil.fromJson(json, LocalDateTime.class);
        assertEquals(dateTime, deserialized);
    }

    @Test
    void testLocalDateSerialization() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String json = GsonUtil.toJson(date);
        assertNotNull(json);
        assertTrue(json.contains("2024-01-01"));

        LocalDate deserialized = GsonUtil.fromJson(json, LocalDate.class);
        assertEquals(date, deserialized);
    }

    @Test
    void testLocalTimeSerialization() {
        LocalTime time = LocalTime.of(12, 30, 45);
        String json = GsonUtil.toJson(time);
        assertNotNull(json);
        assertTrue(json.contains("12:30:45"));

        LocalTime deserialized = GsonUtil.fromJson(json, LocalTime.class);
        assertEquals(time, deserialized);
    }

    @Test
    void testExtractJsonFromModelOutput() {
        // Test with JSON in the middle of text
        String input1 = "Some text before {\"key\":\"value\"} some text after";
        String expected1 = "{\"key\":\"value\"}";
        assertEquals(expected1, GsonUtil.extractJsonFromModelOutput(input1));

        // Test with multiple JSON objects
        String input2 = "First part {\"key1\":\"value1\"} Second part {\"key2\":\"value2\"}";
        String expected2 = "{\"key1\":\"value1\"}";
        assertEquals(expected2, GsonUtil.extractJsonFromModelOutput(input2));

        // Test with no JSON
        String input3 = "No JSON here";
        assertEquals(input3, GsonUtil.extractJsonFromModelOutput(input3));

        // Test with empty string
        assertEquals("", GsonUtil.extractJsonFromModelOutput(""));

        String input4 = """
             ```json
            {
              "mediaDescription": "图片展示了一组采访场景，背景是一个户外活动场所，可能是一个展览或博览会。画面中有几位年轻人正在接受采访，他们穿着休闲装，有的背着背包。采访者手持翻译设备，与受访者进行交流。受访者中有人提到来自陕西省西安市，并讨论了某些令人印象深刻的事物。背景中可以看到其他游客和建筑物，环境显得热闹且有序。",
              "outcome": "APPROVED",
              "failureReason": ""
            }
            ```\s

            ### 审核说明：
            1. **内容审核**：图片中没有出现政治人物，也没有歧视性内容。
            2. **标准应用**：图片符合预定的观看标准，展示了正常的采访场景，具有一定的知识性和趣味性。
            3. **图像描述**：描述了图片中的主要元素，包括人物、背景和活动氛围。
            4. **结果报告**：根据上述分析，图片审核结果为“APPROVED”，无需提供失败原因。
            """;
        String expected4 = """
            {
              "mediaDescription": "图片展示了一组采访场景，背景是一个户外活动场所，可能是一个展览或博览会。画面中有几位年轻人正在接受采访，他们穿着休闲装，有的背着背包。采访者手持翻译设备，与受访者进行交流。受访者中有人提到来自陕西省西安市，并讨论了某些令人印象深刻的事物。背景中可以看到其他游客和建筑物，环境显得热闹且有序。",
              "outcome": "APPROVED",
              "failureReason": ""
            }
            """;
        assertEquals(expected4.trim(), GsonUtil.extractJsonFromModelOutput(input4).trim());

    }

}

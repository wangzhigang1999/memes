package com.bupt.memes.util;

import com.bupt.memes.model.transport.BroadcastMessage;
import com.bupt.memes.model.transport.OperationType;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaUtilTest {

    @SneakyThrows
    @Test
    void toBytes() {
        byte[] bytes = new byte[] { 1, 2, 3, 4, 5 };
        assertArrayEquals(bytes, KafkaUtil.toBytes(bytes));
        BroadcastMessage originalMessage = BroadcastMessage
                .newBuilder()
                .setId("1")
                .setData(ByteString.copyFrom("Hello World!", Charset.defaultCharset()))
                .setTimestamp(System.currentTimeMillis())
                .setOperation(OperationType.DELETE)
                .build();
        bytes = KafkaUtil.toBytes(originalMessage);
        assertArrayEquals(bytes, originalMessage.toByteArray());
        BroadcastMessage parsed = BroadcastMessage.parseFrom(bytes);
        assertEquals(originalMessage, parsed);
    }
}
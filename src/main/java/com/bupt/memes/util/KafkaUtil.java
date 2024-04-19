package com.bupt.memes.util;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;

import com.bupt.memes.model.transport.MediaMessage;
import com.bupt.memes.model.transport.MediaType;
import com.google.protobuf.ByteString;

import lombok.SneakyThrows;

public class KafkaUtil {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(KafkaUtil.class);
    public final static Properties PROPS = new Properties();

    public final static String TOPIC = "original-msg";

    private volatile static KafkaProducer<String, byte[]> producer;

    static {
        PROPS.put("bootstrap.servers", System.getenv("BOOTSTRAP_ENDPOINT"));
        PROPS.put("sasl.mechanism", "SCRAM-SHA-512");
        PROPS.put("security.protocol", "SASL_SSL");

        var username = System.getenv("UPSTASH_KAFKA_USERNAME");
        var password = System.getenv("UPSTASH_KAFKA_PASSWORD");

        String saslJaasConfig = """
                org.apache.kafka.common.security.scram.ScramLoginModule required 
                username="%s"
                password="%s";
                """.formatted(username, password);
        PROPS.put("sasl.jaas.config", saslJaasConfig);
        PROPS.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        PROPS.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        init();
        logger.warn(saslJaasConfig);
        logger.info("Kafka producer init success");
    }

    private static void init() {
        if (producer == null) {
            synchronized (KafkaUtil.class) {
                if (producer == null) {
                    producer = new KafkaProducer<>(PROPS);
                }
            }
        }
    }

    @SneakyThrows
    public static void send(MediaMessage message) {
        producer.send(new ProducerRecord<>(TOPIC, message.toByteArray()));
    }

    public static void send(String id, String text) {
        MediaMessage message = MediaMessage.newBuilder()
                .setId(id)
                .setMediaType(MediaType.TEXT)
                .setData(ByteString.copyFrom(text.getBytes()))
                .setTimestamp(System.currentTimeMillis())
                .build();
        send(message);
    }

    public static void send(String id, byte[] data) {
        MediaMessage message = MediaMessage.newBuilder()
                .setId(id)
                .setMediaType(MediaType.IMAGE)
                .setData(ByteString.copyFrom(data))
                .setTimestamp(System.currentTimeMillis())
                .build();
        send(message);
    }

    public static void main(String[] args) {
        MediaMessage message = MediaMessage.newBuilder()
                .setMediaType(MediaType.TEXT)
                .setData(ByteString.copyFrom("data".getBytes()))
                .setTimestamp(System.currentTimeMillis())
                .build();

        for (int i = 0; i < 10; i++) {
            send(message);
            System.out.println("send success");
        }

    }

}

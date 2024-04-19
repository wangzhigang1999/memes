package com.bupt.memes.util;

import com.bupt.memes.model.transport.MediaMessage;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaProduce {

    public final static Properties props = new Properties();

    public final static String TOPIC = "original-msg";

    private volatile static KafkaProducer<String, byte[]> producer;

    static {
//        PROPS.put("bootstrap.servers", System.getenv("BOOTSTRAP_ENDPOINT"));
//        PROPS.put("sasl.mechanism", "SCRAM-SHA-512");
//        PROPS.put("security.protocol", "SASL_SSL");
//
//        var username = System.getenv("UPSTASH_KAFKA_USERNAME");
//        var password = System.getenv("UPSTASH_KAFKA_PASSWORD");
//
//        PROPS.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required " + "username=\"" + username + "\" " + "password=\"" + password + "\";");
//        PROPS.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        PROPS.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("bootstrap.servers", "https://sunny-liger-14783-eu1-kafka.upstash.io:9092");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"c3VubnktbGlnZXItMTQ3ODMkLf40MJ1XsxLlWfNoDtj56mmD4bbFWcScDeFw5jQ\" password=\"OGVmYmQzZjktZTcyYS00ZGZmLTg1ZWEtNTJjYWMxODU4MmYx\";");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        init();
    }

    private static void init() {
        if (producer == null) {
            synchronized (KafkaProduce.class) {
                if (producer == null) {
                    producer = new KafkaProducer<>(props);
                }
            }
        }
    }

    @SneakyThrows
    public static boolean send(MediaMessage message) {
        Future<RecordMetadata> send = producer.send(new ProducerRecord<>(TOPIC, message.toByteArray()));
        RecordMetadata metadata = send.get();
        return metadata.hasOffset();
    }

    public static void main(String[] args) {
        MediaMessage message = MediaMessage.newBuilder()
                .setType(MediaMessage.MediaType.newBuilder().setType("Image").build())
                .setData(ByteString.copyFrom("data".getBytes()))
                .setTimestamp(System.currentTimeMillis())
                .build();

        for (int i = 0; i < 10; i++) {
            assert send(message);
            System.out.println("send success");
        }

    }

}

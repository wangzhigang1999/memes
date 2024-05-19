package com.bupt.memes.util;

import com.bupt.memes.model.transport.Embedding;
import com.bupt.memes.model.transport.MediaMessage;
import com.bupt.memes.model.transport.MediaType;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.bupt.memes.aspect.Audit.INSTANCE_UUID;

public class KafkaUtil {

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(KafkaUtil.class);
    public final static Properties PROPS = new Properties();

    public final static String ORIGINAL_MSG = "original-msg";
    public final static String EMBEDDING = "embedding";

    private volatile static KafkaProducer<String, byte[]> producer;

    private static final AtomicBoolean producerAlive = new AtomicBoolean(true);

    static {
        try {
            init();
        } catch (Exception e) {
            logger.error("Kafka producer init failed");
        }
    }

    @SneakyThrows
    private static void init() {
        PROPS.put("sasl.mechanism", "SCRAM-SHA-512");
        PROPS.put("security.protocol", "SASL_SSL");
        PROPS.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        PROPS.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        PROPS.put("bootstrap.servers", System.getenv("BOOTSTRAP_ENDPOINT"));
        var username = System.getenv("UPSTASH_KAFKA_USERNAME");
        var password = System.getenv("UPSTASH_KAFKA_PASSWORD");

        String saslJaasConfig = """
                org.apache.kafka.common.security.scram.ScramLoginModule required
                username="%s"
                password="%s";
                """.formatted(username, password);
        PROPS.put("sasl.jaas.config", saslJaasConfig);

        if (producer == null) {
            synchronized (KafkaUtil.class) {
                if (producer == null) {
                    producer = new KafkaProducer<>(PROPS);
                }
            }
        }
        logger.info("Kafka producer init success");
    }

    @SneakyThrows
    public static void send(MediaMessage message) {
        if (!producerAlive.get()) {
            logger.error("Kafka producer is not alive,maybe the producer is closed");
            return;
        }
        producer.send(new ProducerRecord<>(ORIGINAL_MSG, message.toByteArray()));
    }

    public static void send(String id, String text) {
        MediaMessage message = MediaMessage.newBuilder().setId(id).setMediaType(MediaType.TEXT).setData(ByteString.copyFrom(text.getBytes())).setTimestamp(System.currentTimeMillis()).build();
        send(message);
    }

    public static void send(String id, byte[] data) {
        MediaMessage message = MediaMessage.newBuilder().setId(id).setMediaType(MediaType.IMAGE).setData(ByteString.copyFrom(data)).setTimestamp(System.currentTimeMillis()).build();
        send(message);
    }

    public static void flush() {
        if (producer == null) {
            logger.error("Kafka producer is null, maybe the producer is not init");
            return;
        }
        producer.flush();
    }

    public static KafkaConsumer<String, byte[]> getConsumer(String topic, String groupId) {
        var props = new Properties();
        props.putAll(PROPS);
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("auto.offset.reset", "earliest");
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(java.util.Collections.singletonList(topic));
        return consumer;
    }

    public static KafkaConsumer<String, byte[]> getConsumer(String topic) {
        return getConsumer(topic, INSTANCE_UUID);
    }

    public static void close() {
        logger.warn("Kafka producer is closing.............");
        boolean compared = producerAlive.compareAndSet(true, false);
        if (!compared || producer == null) {
            return;
        }
        flush();
        producer.close();
    }

    public static void main() {
        var consumer = KafkaUtil.getConsumer("embedding");
        do {
            try {
                var records = consumer.poll(Duration.ZERO);
                for (var record : records) {
                    Embedding embedding = Embedding.parseFrom(record.value());
                    System.out.println(embedding.getDataList());
                }
            } catch (Exception e) {
                logger.error("Error while consuming messages", e);
                consumer = null;
            }

        } while (consumer != null);
    }

}

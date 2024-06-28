package com.memes.schedule;

import com.memes.util.KafkaUtil;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
public class KafkaFlush {
    @Scheduled(fixedRate = 5000)
    public void flush() {
        KafkaUtil.flush();
    }

    @PreDestroy
    public void destroy() {
        KafkaUtil.close();
    }

}

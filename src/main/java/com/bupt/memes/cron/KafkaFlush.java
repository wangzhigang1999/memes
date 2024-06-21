package com.bupt.memes.cron;

import com.bupt.memes.util.KafkaUtil;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
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

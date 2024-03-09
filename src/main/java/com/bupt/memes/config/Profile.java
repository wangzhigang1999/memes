package com.bupt.memes.config;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class Profile {

    @Value("${perf.url}")
    public String perfUrl;
    @Value("${perf.username}")
    public String perfUsername;
    @Value("${perf.password}")
    public String perfPassword;

    Logger logger = org.slf4j.LoggerFactory.getLogger(Profile.class);

    @PostConstruct
    public void init() {

        // if windows, skip
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            logger.warn("Windows OS, skipping configuration of Pyroscope");
            return;
        }

        if (perfUrl == null || perfUrl.isEmpty()) {
            logger.warn("Pyroscope is not configured, skipping");
            return;
        }
        PyroscopeAgent.start(
                new Config.Builder()
                        .setApplicationName("memes")
                        .setProfilingEvent(EventType.ITIMER)
                        .setFormat(Format.JFR)
                        .setServerAddress(perfUrl)
                        .setBasicAuthUser(perfUsername)
                        .setBasicAuthPassword(perfPassword)
                        .build());
        logger.info("Pyroscope is configured");
    }

}

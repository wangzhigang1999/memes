package com.memes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Memes {
    public static void main(String[] args) {
        SpringApplication.run(Memes.class, args);
    }
}

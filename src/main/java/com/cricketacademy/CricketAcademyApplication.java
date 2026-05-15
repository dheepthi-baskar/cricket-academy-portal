package com.cricketacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CricketAcademyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CricketAcademyApplication.class, args);
    }
}

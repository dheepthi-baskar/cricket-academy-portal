package com.cricketacademy.config;

import com.cricketacademy.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, NotificationProperties.class})
public class ApplicationConfig {}

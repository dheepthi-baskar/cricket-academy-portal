package com.cricketacademy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "academy.notifications")
public record NotificationProperties(boolean enabled) {}

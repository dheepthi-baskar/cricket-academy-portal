package com.cricketacademy.service;

import com.cricketacademy.config.NotificationProperties;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationProperties properties;

    public NotificationService(NotificationProperties properties) {
        this.properties = properties;
    }

    @Async("notificationExecutor")
    public CompletableFuture<Void> notifyRecipients(List<String> emails, String subject, String body) {
        if (!properties.enabled()) {
            log.debug("Notifications disabled; skipping subject={}", subject);
            return CompletableFuture.completedFuture(null);
        }
        log.info("Dispatching async notification '{}' to {} recipients", subject, emails.size());
        for (String email : emails) {
            log.info("Notify {} :: {}", email, body);
        }
        return CompletableFuture.completedFuture(null);
    }
}

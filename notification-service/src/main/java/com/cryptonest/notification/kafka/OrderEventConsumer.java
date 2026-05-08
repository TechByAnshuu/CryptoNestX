package com.cryptonest.notification.kafka;

import com.cryptonest.notification.service.EmailService;
import com.cryptonest.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final EmailService emailService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void consumeOrderEvent(Map<String, Object> event) {
        log.info("Received order event: {}", event);
        
        try {
            String userIdStr = (String) event.get("userId");
            if (userIdStr == null) return;
            
            UUID userId = UUID.fromString(userIdStr);
            String userEmail = (String) event.get("userEmail");
            String symbol = (String) event.get("symbol");
            String type = (String) event.get("type");
            String status = (String) event.get("status");
            Object quantityObj = event.get("quantity");
            Object priceObj = event.get("price");

            String message = String.format("Your %s order for %s %s at $%s was %s.",
                    type, quantityObj, symbol, priceObj, status);

            // Save to DB
            notificationService.createNotification(userId, "ORDER_UPDATE", message);

            // Send Email
            if (userEmail != null && !userEmail.isEmpty()) {
                String subject = "CryptoNestX Order Update: " + status;
                emailService.sendEmail(userEmail, subject, message);
            }
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }
}

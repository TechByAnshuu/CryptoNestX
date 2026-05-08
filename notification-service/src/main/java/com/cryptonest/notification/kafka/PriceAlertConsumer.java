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
public class PriceAlertConsumer {

    private final EmailService emailService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "price-alerts", groupId = "notification-group")
    public void consumePriceAlert(Map<String, Object> event) {
        log.info("Received price alert event: {}", event);

        try {
            String userIdStr = (String) event.get("userId");
            if (userIdStr == null) return;

            UUID userId = UUID.fromString(userIdStr);
            String userEmail = (String) event.get("userEmail");
            String symbol = (String) event.get("symbol");
            Object targetPrice = event.get("targetPrice");
            Object currentPrice = event.get("currentPrice");

            String message = String.format("Price Alert: %s has reached $%s (Target: $%s)",
                    symbol, currentPrice, targetPrice);

            // Save to DB
            notificationService.createNotification(userId, "PRICE_ALERT", message);

            // Send Email
            if (userEmail != null && !userEmail.isEmpty()) {
                String subject = "CryptoNestX Price Alert: " + symbol;
                emailService.sendEmail(userEmail, subject, message);
            }
        } catch (Exception e) {
            log.error("Error processing price alert event: {}", e.getMessage(), e);
        }
    }
}

package com.cryptonest.notification.kafka;

import com.cryptonest.notification.service.EmailService;
import com.cryptonest.notification.service.NotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceAlertConsumer {

    private final EmailService emailService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "price-alerts", groupId = "notification-group")
    public void consume(PriceAlertEvent event) {
        log.info("Received price alert for user: {} coin: {}", event.getUserId(), event.getCoinSymbol());

        String email = event.getEmail();
        if (email == null || email.isBlank()) {
            email = "user_" + event.getUserId().toString().substring(0, 8) + "@cryptonestx.demo";
        }

        String plainMessage = String.format("Price Alert: %s %s $%s. Current price: $%s",
                event.getCoinSymbol(), event.getDirection(), event.getTargetPrice(), event.getCurrentPrice());

        // Save to DB
        notificationService.createNotification(event.getUserId(), "PRICE_ALERT", plainMessage);

        // Send Email
        String subject = String.format("🚨 CryptoNestX Alert: %s hit $%s", event.getCoinSymbol(), event.getCurrentPrice());
        String body = String.format(
                "<h3>CryptoNestX Price Alert</h3>" +
                "<p>Your alert for <b>%s</b> <b>%s</b> $%.2f has triggered.</p>" +
                "<p>Current price: <b>$%.2f</b></p>",
                event.getCoinSymbol(),
                event.getDirection(),
                event.getTargetPrice(),
                event.getCurrentPrice()
        );

        emailService.sendHtmlEmail(email, subject, body);
    }
}

@Data
class PriceAlertEvent {
    private UUID userId;
    private String email;
    private String coinSymbol;
    private BigDecimal targetPrice;
    private BigDecimal currentPrice;
    private String direction;
}

package com.cryptonest.portfolio.service;

import com.cryptonest.portfolio.dto.PriceAlertEvent;
import com.cryptonest.portfolio.entity.PriceAlert;
import com.cryptonest.portfolio.kafka.AlertEventProducer;
import com.cryptonest.portfolio.repository.PriceAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertCheckerScheduler {

    private final PriceAlertRepository alertRepository;
    private final PriceService priceService;
    private final AlertEventProducer alertEventProducer;

    @Scheduled(fixedRate = 60000) // every 60 seconds
    @Transactional
    public void checkAlerts() {
        List<PriceAlert> activeAlerts = alertRepository.findByIsActiveTrue();
        if (activeAlerts.isEmpty()) return;

        log.info("Checking {} active price alerts", activeAlerts.size());

        for (PriceAlert alert : activeAlerts) {
            try {
                BigDecimal currentPrice = priceService.fetchLivePrice(alert.getCoinId());
                boolean triggered = false;

                if ("ABOVE".equalsIgnoreCase(alert.getDirection()) && currentPrice.compareTo(alert.getTargetPrice()) >= 0) {
                    triggered = true;
                } else if ("BELOW".equalsIgnoreCase(alert.getDirection()) && currentPrice.compareTo(alert.getTargetPrice()) <= 0) {
                    triggered = true;
                }

                if (triggered) {
                    alert.setActive(false);
                    alert.setTriggeredAt(Instant.now());
                    alertRepository.save(alert);

                    // Publish Kafka event
                    PriceAlertEvent event = PriceAlertEvent.builder()
                            .userId(alert.getUserId())
                            .email("") // Usually fetched from a user service, omitting for demo
                            .coinSymbol(alert.getCoinSymbol())
                            .targetPrice(alert.getTargetPrice())
                            .currentPrice(currentPrice)
                            .direction(alert.getDirection().toUpperCase())
                            .build();
                    
                    alertEventProducer.publishAlert(event);
                    log.info("Alert triggered for {}: {} at ${}", alert.getUserId(), alert.getCoinSymbol(), currentPrice);
                }
            } catch (Exception e) {
                log.warn("Failed to check alert {}: {}", alert.getId(), e.getMessage());
            }
        }
    }
}

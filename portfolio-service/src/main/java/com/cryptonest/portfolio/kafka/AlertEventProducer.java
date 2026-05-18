package com.cryptonest.portfolio.kafka;

import com.cryptonest.portfolio.dto.PriceAlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAlert(PriceAlertEvent event) {
        log.info("Publishing price alert event to Kafka for user: {}", event.getUserId());
        kafkaTemplate.send("price-alerts", event);
    }
}

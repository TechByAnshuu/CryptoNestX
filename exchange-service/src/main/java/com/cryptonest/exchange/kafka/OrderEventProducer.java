package com.cryptonest.exchange.kafka;

import com.cryptonest.exchange.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void publishOrderEvent(Order order, String userEmail) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", order.getId().toString());
        event.put("userId", order.getUserId().toString());
        event.put("userEmail", userEmail);
        event.put("symbol", order.getSymbol());
        event.put("type", order.getType().name());
        event.put("quantity", order.getQuantity());
        event.put("price", order.getPrice());
        event.put("status", order.getStatus().name());

        log.info("Publishing order event to Kafka topic {}: {}", TOPIC, event);
        kafkaTemplate.send(TOPIC, order.getId().toString(), event);
    }
}

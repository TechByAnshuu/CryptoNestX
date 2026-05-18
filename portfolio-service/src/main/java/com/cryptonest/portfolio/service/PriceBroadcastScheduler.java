package com.cryptonest.portfolio.service;

import com.cryptonest.portfolio.dto.PriceUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class PriceBroadcastScheduler {

    private final SimpMessagingTemplate messagingTemplate;
    private final PriceService priceService;

    // A small list of top coins to broadcast for the simulation
    private static final List<String> TOP_COINS = List.of(
            "BTC", "ETH", "USDT", "SOL", "ADA", "XRP", "DOT", "DOGE"
    );

    @Scheduled(fixedRate = 5000)
    public void broadcastPrices() {
        try {
            List<PriceUpdateDTO> updates = new ArrayList<>();
            for (String symbol : TOP_COINS) {
                updates.add(PriceUpdateDTO.builder()
                        .coinId(symbol.toLowerCase())
                        .symbol(symbol)
                        .priceUsd(priceService.fetchLivePrice(symbol))
                        .change24h(priceService.fetch24hChange(symbol))
                        .updatedAt(Instant.now())
                        .build());
            }

            messagingTemplate.convertAndSend("/topic/prices", updates);
        } catch (Exception e) {
            log.warn("Failed to broadcast prices: {}", e.getMessage());
        }
    }
}

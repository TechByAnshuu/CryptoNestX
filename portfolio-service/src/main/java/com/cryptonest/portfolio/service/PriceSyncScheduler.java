package com.cryptonest.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceSyncScheduler {

    private final PriceService priceService;

    // A list of top coins to sync
    private static final List<String> TOP_COINS = List.of(
            "BTC", "ETH", "USDT", "SOL", "ADA", "XRP", "DOT", "DOGE"
    );

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void syncPrices() {
        long start = System.currentTimeMillis();
        int count = 0;
        
        for (String symbol : TOP_COINS) {
            try {
                // By calling fetchLivePrice, we trigger a cache update due to the TTL expiration.
                // Assuming fetchLivePrice handles cache misses and populates the cache.
                // We're essentially pre-warming the cache.
                priceService.fetchLivePrice(symbol);
                priceService.fetch24hChange(symbol);
                count++;
            } catch (Exception e) {
                log.warn("Failed to sync price for {}: {}", symbol, e.getMessage());
            }
        }
        
        long duration = System.currentTimeMillis() - start;
        log.info("Price sync complete: {} coins updated in {}ms", count, duration);
    }
}

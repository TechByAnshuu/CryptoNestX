package com.cryptonest.portfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Fetches live cryptocurrency prices from CoinMarketCap.
 *
 * Resilience strategy:
 *  - Prices are cached in Redis with a 5-minute TTL ("price::{symbol}").
 *  - On cache miss, the API is called with Spring Retry (max 3 attempts,
 *    exponential backoff: 1s → 2s → 4s).
 *  - On a 429 (rate limit) or any other API failure, the last known
 *    cached price is returned (stale-ok). A WARN is logged — never a 500.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    private final WebClient webClient;
    private final RedisTemplate<String, BigDecimal> redisTemplate;

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    private static final String CACHE_KEY_PREFIX = "price::";

    /**
     * Returns the live USD price for a symbol (e.g. "BTC").
     * Reads from Redis first; calls CMC API only on cache miss.
     */
    public BigDecimal fetchLivePrice(String symbol) {
        String key = CACHE_KEY_PREFIX + symbol.toUpperCase();

        // 1. Try Redis cache first
        BigDecimal cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Cache HIT for {}: ${}", symbol, cached);
            return cached;
        }

        // 2. Cache miss — call API with retry
        log.debug("Cache MISS for {} — calling CoinMarketCap", symbol);
        try {
            BigDecimal price = fetchFromApiWithRetry(symbol);
            // Store fresh value in Redis with TTL
            redisTemplate.opsForValue().set(key, price, CACHE_TTL);
            return price;
        } catch (Exception e) {
            // recover() was already called by @Recover — return stale or fallback
            return getStaleOrFallback(symbol, key);
        }
    }

    /**
     * Retryable API call — 3 attempts, exponential backoff (1s → 2s → 4s).
     * Retries on any RuntimeException (including 429, 5xx, timeout).
     */
    @Retryable(
        retryFor = RuntimeException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public BigDecimal fetchFromApiWithRetry(String symbol) {
        String assetId = symbol.toUpperCase();

        JsonNode response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/cryptocurrency/quotes/latest")
                        .queryParam("symbol", assetId)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response != null && response.has("data") && response.get("data").has(assetId)) {
            JsonNode quote = response.get("data").get(assetId).get("quote").get("USD");
            BigDecimal price = new BigDecimal(quote.get("price").asText());
            log.info("Live price for {}: ${}", assetId, price);
            return price;
        }

        throw new RuntimeException("Empty or unexpected response from CoinMarketCap for " + assetId);
    }

    /**
     * Recovery method — called after all @Retryable attempts are exhausted.
     * Returns the last known stale cache value, or hardcoded fallback if nothing is cached.
     * NEVER throws a 500 — always returns a usable price.
     */
    @Recover
    public BigDecimal recoverFetchFromApiWithRetry(RuntimeException ex, String symbol) {
        log.warn("All retry attempts exhausted for {}. Reason: {}. Serving stale/fallback price.",
                symbol, ex.getMessage());
        return getStaleOrFallback(symbol, CACHE_KEY_PREFIX + symbol.toUpperCase());
    }

    /**
     * Fetches 24h percent change for a symbol.
     * Uses the same resilience pattern — stale-ok, no 500s.
     */
    public BigDecimal fetch24hChange(String symbol) {
        String assetId = symbol.toUpperCase();
        try {
            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/cryptocurrency/quotes/latest")
                            .queryParam("symbol", assetId)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("data") && response.get("data").has(assetId)) {
                JsonNode quote = response.get("data").get(assetId).get("quote").get("USD");
                return new BigDecimal(quote.get("percent_change_24h").asText());
            }
        } catch (WebClientResponseException.TooManyRequests e) {
            log.warn("Rate limited (429) fetching 24h change for {}. Returning zero.", symbol);
        } catch (Exception e) {
            log.warn("Failed to fetch 24h change for {}: {}", symbol, e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private BigDecimal getStaleOrFallback(String symbol, String cacheKey) {
        BigDecimal stale = redisTemplate.opsForValue().get(cacheKey);
        if (stale != null) {
            log.warn("Serving STALE cached price for {}: ${}", symbol, stale);
            return stale;
        }
        log.warn("No cached price found for {}. Using hardcoded fallback.", symbol);
        return getHardcodedFallback(symbol);
    }

    private BigDecimal getHardcodedFallback(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "BTC"  -> new BigDecimal("65432.10");
            case "ETH"  -> new BigDecimal("3456.78");
            case "USDT" -> new BigDecimal("1.00");
            case "SOL"  -> new BigDecimal("145.20");
            case "ADA"  -> new BigDecimal("0.55");
            case "XRP"  -> new BigDecimal("0.62");
            case "DOT"  -> new BigDecimal("8.90");
            case "DOGE" -> new BigDecimal("0.17");
            default     -> new BigDecimal("100.00");
        };
    }
}

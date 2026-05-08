package com.cryptonest.portfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    private final WebClient webClient;

    /**
     * Fetches live USD price for a given symbol (e.g. BTC, ETH) from CoinMarketCap.
     * Endpoint: GET /v1/cryptocurrency/quotes/latest?symbol={symbol}
     * Cached in Redis to prevent API rate limiting.
     */
    @Cacheable(value = "crypto-prices", key = "#a0", unless = "#result == null")
    public BigDecimal fetchLivePrice(String symbol) {
        String assetId = symbol.toUpperCase();

        log.debug("Fetching price for {} from CoinMarketCap", assetId);

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
                BigDecimal price = new BigDecimal(quote.get("price").asText());
                log.info("CoinMarketCap price for {}: ${}", assetId, price);
                return price;
            }

        } catch (Exception e) {
            log.error("Failed to fetch price for {} from CoinMarketCap: {}", assetId, e.getMessage());
        }

        // Fallback to static data if API fails or rate limits hit
        return getFallbackPrice(symbol);
    }

    /**
     * Fetches 24h percent change data for a symbol.
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

        } catch (Exception e) {
            log.warn("Failed to fetch 24h change for {}: {}", symbol, e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getFallbackPrice(String symbol) {
        log.warn("Using fallback price for {}", symbol);
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

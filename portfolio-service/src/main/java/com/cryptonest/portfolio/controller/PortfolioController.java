package com.cryptonest.portfolio.controller;

import com.cryptonest.portfolio.dto.PortfolioSummaryDTO;
import com.cryptonest.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final com.cryptonest.portfolio.service.PriceService priceService;

    @GetMapping("/summary/{userId}")
    public ResponseEntity<PortfolioSummaryDTO> getSummary(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(userId));
    }
    
    // In a real implementation, we would extract the userId from the JWT context
    @GetMapping("/summary")
    public ResponseEntity<PortfolioSummaryDTO> getMySummary(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(userId));
    }

    @GetMapping(value = "/prices", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.Map<String, java.math.BigDecimal>> getPrices(@RequestParam("symbols") java.util.List<String> symbols) {
        java.util.Map<String, java.math.BigDecimal> prices = new java.util.HashMap<>();
        for (String symbol : symbols) {
            prices.put(symbol, priceService.fetchLivePrice(symbol));
        }
        return ResponseEntity.ok(prices);
    }

}

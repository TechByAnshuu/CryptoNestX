package com.cryptonest.portfolio.controller;

import com.cryptonest.portfolio.entity.PriceAlert;
import com.cryptonest.portfolio.repository.PriceAlertRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Price Alerts", description = "Automated price threshold alerts")
public class AlertController {

    private final PriceAlertRepository alertRepository;

    @PostMapping
    @Operation(summary = "Create a new price alert")
    public ResponseEntity<PriceAlert> createAlert(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody CreateAlertRequest req) {
        
        PriceAlert alert = PriceAlert.builder()
                .userId(userId)
                .coinId(req.getCoinId())
                .coinSymbol(req.getCoinSymbol())
                .targetPrice(req.getTargetPrice())
                .direction(req.getDirection().toUpperCase())
                .build();
                
        return ResponseEntity.ok(alertRepository.save(alert));
    }

    @GetMapping
    @Operation(summary = "List all active alerts for the user")
    public ResponseEntity<List<PriceAlert>> getActiveAlerts(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(alertRepository.findByUserIdAndIsActiveTrue(userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an active alert")
    public ResponseEntity<Void> cancelAlert(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        
        alertRepository.findById(id).ifPresent(alert -> {
            if (alert.getUserId().equals(userId)) {
                alert.setActive(false);
                alertRepository.save(alert);
            }
        });
        return ResponseEntity.noContent().build();
    }
}

@Data
class CreateAlertRequest {
    private String coinId;
    private String coinSymbol;
    private BigDecimal targetPrice;
    private String direction;
}

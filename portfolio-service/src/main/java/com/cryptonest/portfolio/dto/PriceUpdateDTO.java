package com.cryptonest.portfolio.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/** Real-time price update sent over WebSocket */
@Data
@Builder
public class PriceUpdateDTO {
    private String coinId;
    private String symbol;
    private BigDecimal priceUsd;
    private BigDecimal change24h;
    private Instant updatedAt;
}

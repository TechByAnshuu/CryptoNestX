package com.cryptonest.exchange.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** DTO returned by GET /api/ledger */
@Data
@Builder
public class LedgerDTO {
    private UUID id;
    private String type;        // BUY | SELL
    private String coinId;
    private String coinSymbol;
    private BigDecimal quantity;
    private BigDecimal priceAtTrade;
    private BigDecimal totalValue;
    private BigDecimal fee;
    private String status;
    private Instant createdAt;
}

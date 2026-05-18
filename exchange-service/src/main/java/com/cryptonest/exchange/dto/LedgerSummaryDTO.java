package com.cryptonest.exchange.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/** DTO returned by GET /api/ledger/summary */
@Data
@Builder
public class LedgerSummaryDTO {
    private long totalTrades;
    private BigDecimal totalVolume;
    private BigDecimal totalFeesPaid;
}

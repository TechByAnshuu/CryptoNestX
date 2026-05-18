package com.cryptonest.exchange.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/** Response for GET /api/wallet/balance */
@Data
@Builder
public class WalletBalanceDTO {
    private BigDecimal balance;
    private String currency;
    private Instant lastUpdated;
}

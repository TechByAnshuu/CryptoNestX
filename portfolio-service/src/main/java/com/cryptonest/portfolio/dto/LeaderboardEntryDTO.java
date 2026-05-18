package com.cryptonest.portfolio.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class LeaderboardEntryDTO {
    private int rank;
    private UUID userId;
    private String username;
    private BigDecimal totalValue;
    private BigDecimal pnl;
}

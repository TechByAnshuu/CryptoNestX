package com.cryptonest.portfolio.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PriceAlertEvent {
    private UUID userId;
    private String email;
    private String coinSymbol;
    private BigDecimal targetPrice;
    private BigDecimal currentPrice;
    private String direction;
}

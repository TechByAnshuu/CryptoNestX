package com.cryptonest.exchange.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private String symbol;
    private String type;
    private BigDecimal quantity;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
}

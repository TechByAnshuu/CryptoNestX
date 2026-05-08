package com.cryptonest.portfolio.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PortfolioSummaryDTO {
    private UUID portfolioId;
    private UUID userId;
    private BigDecimal totalValue;
    private BigDecimal totalPnl;
    private BigDecimal totalPnlPercentage;
    private List<HoldingDTO> holdings;
}

package com.cryptonest.portfolio.service;

import com.cryptonest.portfolio.dto.HoldingDTO;
import com.cryptonest.portfolio.dto.PortfolioSummaryDTO;
import com.cryptonest.portfolio.entity.Holding;
import com.cryptonest.portfolio.entity.Portfolio;
import com.cryptonest.portfolio.repository.HoldingRepository;
import com.cryptonest.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final PriceService priceService;

    @Transactional
    public PortfolioSummaryDTO getPortfolioSummary(UUID userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseGet(() -> createPortfolio(userId));

        List<Holding> holdings = holdingRepository.findAllByPortfolioId(portfolio.getId());
        List<HoldingDTO> holdingDTOs = new ArrayList<>();

        BigDecimal totalPortfolioValue = BigDecimal.ZERO;
        BigDecimal totalInvestedValue = BigDecimal.ZERO;

        for (Holding holding : holdings) {
            BigDecimal currentPrice = priceService.fetchLivePrice(holding.getSymbol());
            BigDecimal currentValue = currentPrice.multiply(holding.getQuantity());
            BigDecimal investedValue = holding.getAvgBuyPrice().multiply(holding.getQuantity());
            
            BigDecimal pnl = currentValue.subtract(investedValue);
            BigDecimal pnlPercentage = investedValue.compareTo(BigDecimal.ZERO) == 0 
                ? BigDecimal.ZERO 
                : pnl.divide(investedValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

            totalPortfolioValue = totalPortfolioValue.add(currentValue);
            totalInvestedValue = totalInvestedValue.add(investedValue);

            holdingDTOs.add(HoldingDTO.builder()
                    .holdingId(holding.getId())
                    .symbol(holding.getSymbol())
                    .quantity(holding.getQuantity())
                    .avgBuyPrice(holding.getAvgBuyPrice())
                    .currentPrice(currentPrice)
                    .currentValue(currentValue)
                    .pnl(pnl)
                    .pnlPercentage(pnlPercentage)
                    .build());
        }

        BigDecimal totalPnl = totalPortfolioValue.subtract(totalInvestedValue);
        BigDecimal totalPnlPercentage = totalInvestedValue.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalPnl.divide(totalInvestedValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        portfolio.setTotalValue(totalPortfolioValue);
        portfolioRepository.save(portfolio);

        return PortfolioSummaryDTO.builder()
                .portfolioId(portfolio.getId())
                .userId(userId)
                .totalValue(totalPortfolioValue)
                .totalPnl(totalPnl)
                .totalPnlPercentage(totalPnlPercentage)
                .holdings(holdingDTOs)
                .build();
    }

    @Transactional
    public void updateHolding(UUID userId, String symbol, BigDecimal quantityChange, BigDecimal executionPrice) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseGet(() -> createPortfolio(userId));

        Holding holding = holdingRepository.findByPortfolioIdAndSymbol(portfolio.getId(), symbol)
                .orElseGet(() -> Holding.builder()
                        .portfolio(portfolio)
                        .symbol(symbol)
                        .quantity(BigDecimal.ZERO)
                        .avgBuyPrice(BigDecimal.ZERO)
                        .build());

        BigDecimal newQuantity = holding.getQuantity().add(quantityChange);

        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient quantity for symbol: " + symbol);
        }

        if (quantityChange.compareTo(BigDecimal.ZERO) > 0) {
            // Recalculate average buy price
            BigDecimal totalOldCost = holding.getQuantity().multiply(holding.getAvgBuyPrice());
            BigDecimal totalNewCost = quantityChange.multiply(executionPrice);
            BigDecimal newAvgBuyPrice = totalOldCost.add(totalNewCost).divide(newQuantity, 8, RoundingMode.HALF_UP);
            holding.setAvgBuyPrice(newAvgBuyPrice);
        }

        holding.setQuantity(newQuantity);
        
        if (newQuantity.compareTo(BigDecimal.ZERO) == 0) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }
        
        log.info("Updated holding for user {} - Symbol: {}, Quantity: {}", userId, symbol, newQuantity);
    }

    private Portfolio createPortfolio(UUID userId) {
        Portfolio portfolio = Portfolio.builder()
                .userId(userId)
                .totalValue(BigDecimal.ZERO)
                .build();
        return portfolioRepository.save(portfolio);
    }
}

package com.cryptonest.portfolio.service;

import com.cryptonest.portfolio.dto.LeaderboardEntryDTO;
import com.cryptonest.portfolio.entity.Portfolio;
import com.cryptonest.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService; // to compute live value

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDTO> getLeaderboard() {
        // Fetch all public portfolios
        List<Portfolio> publicPortfolios = portfolioRepository.findByIsPublicTrue();
        
        List<LeaderboardEntryDTO> entries = new ArrayList<>();
        
        for (Portfolio p : publicPortfolios) {
            try {
                // Compute live total value from holdings using Redis prices
                var summary = portfolioService.getPortfolioSummary(p.getUserId());
                BigDecimal totalValue = summary.getTotalValue();
                BigDecimal pnl = totalValue.subtract(p.getInitialDeposit());

                entries.add(LeaderboardEntryDTO.builder()
                        .userId(p.getUserId())
                        .username(p.getUsername() != null ? p.getUsername() : "Anonymous")
                        .totalValue(totalValue.setScale(2, RoundingMode.HALF_UP))
                        .pnl(pnl.setScale(2, RoundingMode.HALF_UP))
                        .build());
            } catch (Exception e) {
                log.warn("Failed to compute leaderboard entry for user {}: {}", p.getUserId(), e.getMessage());
            }
        }
        
        // Sort by totalValue DESC
        entries.sort(Comparator.comparing(LeaderboardEntryDTO::getTotalValue).reversed());
        
        // Assign ranks and limit to 50
        AtomicInteger rank = new AtomicInteger(1);
        return entries.stream()
                .limit(50)
                .peek(e -> e.setRank(rank.getAndIncrement()))
                .toList();
    }
}

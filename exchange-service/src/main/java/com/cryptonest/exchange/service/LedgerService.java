package com.cryptonest.exchange.service;

import com.cryptonest.exchange.dto.LedgerDTO;
import com.cryptonest.exchange.dto.LedgerSummaryDTO;
import com.cryptonest.exchange.dto.PlaceOrderRequest;
import com.cryptonest.exchange.entity.TransactionLedger;
import com.cryptonest.exchange.repository.TransactionLedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerService {

    private final TransactionLedgerRepository ledgerRepository;

    /**
     * Records a completed trade in the immutable transaction ledger.
     * Called by OrderService after every successful order execution.
     */
    @Transactional
    public void recordTrade(UUID userId, PlaceOrderRequest req, BigDecimal executedPrice) {
        BigDecimal totalValue = executedPrice.multiply(req.getQuantity());
        BigDecimal fee        = totalValue.multiply(new BigDecimal("0.001")); // 0.1%

        TransactionLedger entry = TransactionLedger.builder()
                .userId(userId)
                .type(req.getType().name())
                .coinId(req.getSymbol().toLowerCase())
                .coinSymbol(req.getSymbol().toUpperCase())
                .quantity(req.getQuantity())
                .priceAtTrade(executedPrice)
                .totalValue(totalValue)
                .fee(fee)
                .status("COMPLETED")
                .build();

        ledgerRepository.save(entry);
        log.info("Ledger: recorded {} {} for user {}", req.getType(), req.getSymbol(), userId);
    }

    /** Returns paginated trade history for a user, newest first. */
    @Transactional(readOnly = true)
    public Page<LedgerDTO> getUserHistory(UUID userId, int page, int size) {
        return ledgerRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(this::toDTO);
    }

    /** Returns trades within a date range. */
    @Transactional(readOnly = true)
    public List<LedgerDTO> getUserHistoryByDateRange(UUID userId, Instant from, Instant to) {
        return ledgerRepository
                .findByUserIdAndCreatedAtBetween(userId, from, to)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /** Returns aggregate summary: total trades, volume, and fees paid. */
    @Transactional(readOnly = true)
    public LedgerSummaryDTO getSummary(UUID userId) {
        Object[] row = ledgerRepository.findSummaryByUserId(userId);
        if (row == null || row[0] == null) {
            return LedgerSummaryDTO.builder()
                    .totalTrades(0)
                    .totalVolume(BigDecimal.ZERO)
                    .totalFeesPaid(BigDecimal.ZERO)
                    .build();
        }
        return LedgerSummaryDTO.builder()
                .totalTrades(((Number) row[0]).longValue())
                .totalVolume(row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO)
                .totalFeesPaid(row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO)
                .build();
    }

    // ── Private ────────────────────────────────────────────────────────────

    private LedgerDTO toDTO(TransactionLedger t) {
        return LedgerDTO.builder()
                .id(t.getId())
                .type(t.getType())
                .coinId(t.getCoinId())
                .coinSymbol(t.getCoinSymbol())
                .quantity(t.getQuantity())
                .priceAtTrade(t.getPriceAtTrade())
                .totalValue(t.getTotalValue())
                .fee(t.getFee())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build();
    }
}

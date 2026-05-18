package com.cryptonest.exchange.repository;

import com.cryptonest.exchange.entity.TransactionLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionLedgerRepository extends JpaRepository<TransactionLedger, UUID> {

    Page<TransactionLedger> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<TransactionLedger> findByUserIdAndCreatedAtBetween(
            UUID userId, Instant from, Instant to);

    @Query("""
        SELECT COUNT(t)          AS totalTrades,
               SUM(t.totalValue) AS totalVolume,
               SUM(t.fee)        AS totalFees
        FROM TransactionLedger t
        WHERE t.userId = :userId
    """)
    Object[] findSummaryByUserId(@Param("userId") UUID userId);
}

package com.cryptonest.exchange.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable audit trail entry for every executed trade (BUY or SELL).
 * Written once after order execution — never updated.
 */
@Entity
@Table(name = "transaction_ledger",
    indexes = {
        @Index(name = "idx_ledger_user_id",    columnList = "user_id"),
        @Index(name = "idx_ledger_created_at", columnList = "created_at DESC")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** BUY or SELL */
    @Column(nullable = false, length = 10)
    private String type;

    /** e.g. "bitcoin", "ethereum" */
    @Column(name = "coin_id", nullable = false, length = 50)
    private String coinId;

    /** e.g. "BTC", "ETH" */
    @Column(name = "coin_symbol", nullable = false, length = 10)
    private String coinSymbol;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal quantity;

    @Column(name = "price_at_trade", nullable = false, precision = 18, scale = 2)
    private BigDecimal priceAtTrade;

    @Column(name = "total_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalValue;

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "COMPLETED";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

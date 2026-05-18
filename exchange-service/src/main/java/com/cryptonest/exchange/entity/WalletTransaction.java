package com.cryptonest.exchange.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Records individual deposit and withdrawal events against a wallet.
 */
@Entity
@Table(name = "wallet_transaction",
    indexes = {
        @Index(name = "idx_wallet_txn_wallet_id",   columnList = "wallet_id"),
        @Index(name = "idx_wallet_txn_created_at", columnList = "created_at DESC")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    /** DEPOSIT or WITHDRAWAL */
    @Column(nullable = false, length = 10)
    private String type;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "COMPLETED";

    /** Optional external reference or note */
    @Column(length = 100)
    private String reference;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

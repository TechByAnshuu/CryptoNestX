package com.cryptonest.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "coin_id", nullable = false, length = 50)
    private String coinId;

    @Column(name = "coin_symbol", nullable = false, length = 10)
    private String coinSymbol;

    @Column(name = "target_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal targetPrice;

    @Column(nullable = false, length = 5)
    private String direction; // ABOVE or BELOW

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "triggered_at")
    private Instant triggeredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

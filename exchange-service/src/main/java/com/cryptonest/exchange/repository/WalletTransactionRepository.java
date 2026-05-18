package com.cryptonest.exchange.repository;

import com.cryptonest.exchange.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {

    Page<WalletTransaction> findByWalletUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}

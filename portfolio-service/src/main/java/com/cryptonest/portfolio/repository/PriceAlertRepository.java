package com.cryptonest.portfolio.repository;

import com.cryptonest.portfolio.entity.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, UUID> {
    List<PriceAlert> findByUserIdAndIsActiveTrue(UUID userId);
    List<PriceAlert> findByIsActiveTrue();
}

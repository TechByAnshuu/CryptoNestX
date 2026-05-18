package com.cryptonest.portfolio.repository;

import com.cryptonest.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    Optional<Portfolio> findByUserId(UUID userId);
    
    List<Portfolio> findByIsPublicTrue();
}

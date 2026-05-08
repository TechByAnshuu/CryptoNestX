package com.cryptonest.exchange.service;

import com.cryptonest.exchange.entity.Wallet;
import com.cryptonest.exchange.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final StripeService stripeService;

    @Transactional
    public Wallet getOrCreateWallet(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet newWallet = Wallet.builder()
                            .userId(userId)
                            .balance(BigDecimal.ZERO)
                            .build();
                    return walletRepository.save(newWallet);
                });
    }

    @Transactional
    public Wallet topUp(UUID userId, BigDecimal amount, String paymentMethodId) {
        // In a real app, verify the payment with Stripe first
        boolean paymentSuccessful = stripeService.processPayment(amount, paymentMethodId);
        
        if (!paymentSuccessful) {
            throw new IllegalStateException("Payment failed");
        }

        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        
        log.info("Topped up wallet for user {}. New balance: {}", userId, wallet.getBalance());
        return walletRepository.save(wallet);
    }

    @Transactional
    public void debit(UUID userId, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(userId);
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds in wallet");
        }
        
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        log.info("Debited {} from wallet for user {}. New balance: {}", amount, userId, wallet.getBalance());
    }

    @Transactional
    public void credit(UUID userId, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        log.info("Credited {} to wallet for user {}. New balance: {}", amount, userId, wallet.getBalance());
    }
}

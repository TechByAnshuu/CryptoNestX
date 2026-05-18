package com.cryptonest.exchange.service;

import com.cryptonest.exchange.dto.WalletBalanceDTO;
import com.cryptonest.exchange.entity.Wallet;
import com.cryptonest.exchange.entity.WalletTransaction;
import com.cryptonest.exchange.repository.WalletRepository;
import com.cryptonest.exchange.repository.WalletTransactionRepository;
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
    private final WalletTransactionRepository walletTxnRepository;

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

    /** Simulated demo deposit — no payment gateway. Max $100,000 per deposit. */
    @Transactional
    public Wallet deposit(UUID userId, BigDecimal amount) {
        if (amount.compareTo(new java.math.BigDecimal("100000")) > 0) {
            throw new IllegalArgumentException("Demo deposit limit is $100,000 per transaction");
        }
        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        walletTxnRepository.save(WalletTransaction.builder()
                .wallet(wallet).type("DEPOSIT").amount(amount).build());

        log.info("Deposited {} for user {}. New balance: {}", amount, userId, wallet.getBalance());
        return wallet;
    }

    /** Simulated demo withdrawal — validates sufficient balance. */
    @Transactional
    public Wallet withdraw(UUID userId, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance for withdrawal");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        walletTxnRepository.save(WalletTransaction.builder()
                .wallet(wallet).type("WITHDRAWAL").amount(amount).build());

        log.info("Withdrew {} for user {}. New balance: {}", amount, userId, wallet.getBalance());
        return wallet;
    }

    /** Returns balance DTO for GET /api/wallet/balance */
    @Transactional(readOnly = true)
    public WalletBalanceDTO getBalance(UUID userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return WalletBalanceDTO.builder()
                .balance(wallet.getBalance())
                .currency("USD")
                .lastUpdated(wallet.getUpdatedAt().toInstant(java.time.ZoneOffset.UTC))
                .build();
    }
}

package com.cryptonest.exchange.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class StripeService {

    /**
     * Simulated Stripe payment processing.
     */
    public boolean processPayment(BigDecimal amount, String paymentMethodId) {
        log.info("Processing Stripe payment of ${} with method {}", amount, paymentMethodId);
        // Simulate a delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Always succeed for this simulated project
        return true;
    }
}

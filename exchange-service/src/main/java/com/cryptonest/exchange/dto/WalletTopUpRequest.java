package com.cryptonest.exchange.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTopUpRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.00", message = "Minimum top-up amount is $10.00")
    private BigDecimal amount;
    
    // In a real app, this would contain Stripe token/payment method ID
    private String paymentMethodId;
}

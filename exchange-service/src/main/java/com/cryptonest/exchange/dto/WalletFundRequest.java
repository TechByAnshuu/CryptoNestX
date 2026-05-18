package com.cryptonest.exchange.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/** Request body for POST /api/wallet/deposit and /api/wallet/withdraw */
@Data
public class WalletFundRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00",        message = "Minimum amount is $1.00")
    @DecimalMax(value = "100000.00",   message = "Maximum demo deposit is $100,000")
    private BigDecimal amount;
}

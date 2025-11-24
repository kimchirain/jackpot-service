package com.sportygroup.jackpot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BetRequest(
    @NotNull(message = "Bet ID is required")
    String betId,
    
    @NotNull(message = "User ID is required")
    String userId,
    
    @NotNull(message = "Jackpot ID is required")
    Long jackpotId,
    
    @NotNull(message = "Bet amount is required")
    @Positive(message = "Bet amount must be positive")
    BigDecimal betAmount,
    
    String correlationId
) {
    //setting correlation ID if null
    public BetRequest {
        // add validation or transformation here
    }
}

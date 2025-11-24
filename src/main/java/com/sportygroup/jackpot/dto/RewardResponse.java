package com.sportygroup.jackpot.dto;

import java.math.BigDecimal;

public record RewardResponse(
    boolean won,
    BigDecimal rewardAmount,
    String message
) {

    public static RewardResponse winner(BigDecimal amount) {
        return new RewardResponse(true, amount, "Congratulations! You won the jackpot!");
    }
    
    public static RewardResponse noWin() {
        return new RewardResponse(false, BigDecimal.ZERO, "Better luck next time!");
    }
    
    public static RewardResponse notFound() {
        return new RewardResponse(false, BigDecimal.ZERO, "Bet not found or not eligible for reward");
    }
}

package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FixedContributionStrategy implements ContributionStrategy {
    
    @Override
    public BigDecimal calculateContribution(Jackpot jackpot, BigDecimal betAmount) {
        return betAmount
                .multiply(jackpot.getContributionPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}

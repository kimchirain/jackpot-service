package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VariableContributionStrategy implements ContributionStrategy {
    
    @Override
    public BigDecimal calculateContribution(Jackpot jackpot, BigDecimal betAmount) {
        // calculate the decay based on how much the pool has grown
        BigDecimal poolGrowth = jackpot.getCurrentPoolValue()
                .subtract(jackpot.getInitialPoolValue());
        
        BigDecimal decayFactor = poolGrowth
                .multiply(jackpot.getContributionDecayRate())
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        
        // effective percentage = base percentage - decay
        BigDecimal effectivePercentage = jackpot.getContributionPercentage()
                .subtract(decayFactor)
                .max(BigDecimal.ONE); // Minimum 1%
        
        return betAmount
                .multiply(effectivePercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}

package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component
public class VariableRewardStrategy implements RewardStrategy {
    
    private final Random random = new Random();
    
    @Override
    public boolean determineWin(Jackpot jackpot) {
        BigDecimal poolLimit = jackpot.getRewardPoolLimit();
        
        // if pool reached limit, 100% chance
        if (jackpot.getCurrentPoolValue().compareTo(poolLimit) >= 0) {
            return true;
        }
        
        //progressive chance based on pool growth
        BigDecimal poolRatio = jackpot.getCurrentPoolValue()
                .divide(poolLimit, 4, RoundingMode.HALF_UP);
        
        BigDecimal baseChance = jackpot.getRewardPercentage();
        BigDecimal progressiveBonus = BigDecimal.valueOf(100)
                .subtract(baseChance)
                .multiply(poolRatio);
        
        BigDecimal totalChance = baseChance.add(progressiveBonus);
        
        double chance = totalChance.doubleValue();
        double roll = random.nextDouble() * 100;
        return roll < chance;
    }
}

package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class FixedRewardStrategy implements RewardStrategy {
    
    private final Random random = new Random();
    
    @Override
    public boolean determineWin(Jackpot jackpot) {
        double chance = jackpot.getRewardPercentage().doubleValue();
        double roll = random.nextDouble() * 100;
        return roll < chance;
    }
}

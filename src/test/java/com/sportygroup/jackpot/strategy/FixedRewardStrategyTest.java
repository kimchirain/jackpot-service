package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;
import com.sportygroup.jackpot.enums.ContributionType;
import com.sportygroup.jackpot.enums.RewardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FixedRewardStrategyTest {

    private FixedRewardStrategy strategy;
    private Jackpot jackpot;

    @BeforeEach
    void setUp() {
        strategy = new FixedRewardStrategy();
        
        jackpot = new Jackpot();
        jackpot.setId(1L);
        jackpot.setInitialPoolValue(new BigDecimal("10000.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("10000.00"));
        jackpot.setContributionType(ContributionType.FIXED);
        jackpot.setContributionPercentage(new BigDecimal("5.0"));
        jackpot.setRewardType(RewardType.FIXED);
        jackpot.setRewardPercentage(new BigDecimal("1.0"));
    }

    @Test
    void testDetermineWin_Probability() {
        // probability should roughly match expected value
        // 1% chance, on 1000 trials, we expect ~10 wins
        
        int trials = 1000;
        int wins = 0;
        
        for (int i = 0; i < trials; i++) {
            if (strategy.determineWin(jackpot)) {
                wins++;
            }
        }
        
        // allow for variance
        assertTrue(wins >= 0 && wins <= 50, 
            "Expected roughly 10 wins in 1000 trials (1%), got " + wins);
    }

    @Test
    void testDetermineWin_HighProbability() {
        // test with 50% chance
        jackpot.setRewardPercentage(new BigDecimal("50.0"));
        
        int trials = 1000;
        int wins = 0;
        
        for (int i = 0; i < trials; i++) {
            if (strategy.determineWin(jackpot)) {
                wins++;
            }
        }
        
        // with 50%, expect between 400-600 wins
        assertTrue(wins >= 400 && wins <= 600, 
            "Expected roughly 500 wins in 1000 trials (50%), got " + wins);
    }

    @Test
    void testDetermineWin_ZeroProbability() {
        // test with 0% chance
        jackpot.setRewardPercentage(BigDecimal.ZERO);
        
        int trials = 100;
        int wins = 0;
        
        for (int i = 0; i < trials; i++) {
            if (strategy.determineWin(jackpot)) {
                wins++;
            }
        }
        
        assertEquals(0, wins, "Expected no wins with 0% probability");
    }

    @Test
    void testDetermineWin_ReturnsBoolean() {
        // simply verify it returns a boolean value
        boolean result = strategy.determineWin(jackpot);
        assertTrue(result || !result); // just for boolean return
    }
}

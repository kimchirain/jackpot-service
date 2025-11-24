package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;
import com.sportygroup.jackpot.enums.ContributionType;
import com.sportygroup.jackpot.enums.RewardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VariableRewardStrategyTest {

    private VariableRewardStrategy strategy;
    private Jackpot jackpot;

    @BeforeEach
    void setUp() {
        strategy = new VariableRewardStrategy();
        
        jackpot = new Jackpot();
        jackpot.setId(1L);
        jackpot.setInitialPoolValue(new BigDecimal("5000.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("5000.00"));
        jackpot.setContributionType(ContributionType.VARIABLE);
        jackpot.setContributionPercentage(new BigDecimal("10.0"));
        jackpot.setRewardType(RewardType.VARIABLE);
        jackpot.setRewardPercentage(new BigDecimal("0.1"));
        jackpot.setRewardPoolLimit(new BigDecimal("50000.00"));
    }

    @Test
    void testDetermineWin_AtPoolLimit_AlwaysWins() {
        // setup pool
        jackpot.setCurrentPoolValue(new BigDecimal("50000.00"));

        // at limit, should always win (100% chance)
        for (int i = 0; i < 10; i++) {
            assertTrue(strategy.determineWin(jackpot), 
                "Should always win when pool reaches limit");
        }
    }

    @Test
    void testDetermineWin_AbovePoolLimit_AlwaysWins() {
        // setup pool
        jackpot.setCurrentPoolValue(new BigDecimal("60000.00"));

        for (int i = 0; i < 10; i++) {
            assertTrue(strategy.determineWin(jackpot), 
                "Should always win when pool exceeds limit");
        }
    }

    @Test
    void testDetermineWin_AtInitialPool_LowProbability() {
        // at initial pool (5000), with limit 50000
        // ratio = 5000/50000 = 0.1
        
        int trials = 1000;
        int wins = 0;
        
        for (int i = 0; i < trials; i++) {
            if (strategy.determineWin(jackpot)) {
                wins++;
            }
        }
        
        // allow variance (10%)
        assertTrue(wins >= 50 && wins <= 200, 
            "Expected roughly 100 wins in 1000 trials (~10%), got " + wins);
    }

    @Test
    void testDetermineWin_HalfwayToLimit() {
        // setup pool
        jackpot.setCurrentPoolValue(new BigDecimal("25000.00")); // 50% to limit
        
        int trials = 1000;
        int wins = 0;
        
        for (int i = 0; i < trials; i++) {
            if (strategy.determineWin(jackpot)) {
                wins++;
            }
        }
        
        // at 50% to limit, chance should be higher
        assertTrue(wins >= 400 && wins <= 600,
            "Expected roughly 500 wins in 1000 trials (~50%), got " + wins);
    }

    @Test
    void testDetermineWin_NearLimit() {
        // setup pool
        jackpot.setCurrentPoolValue(new BigDecimal("49000.00")); // 98% to limit
        
        int trials = 100;
        int wins = 0;
        
        for (int i = 0; i < trials; i++) {
            if (strategy.determineWin(jackpot)) {
                wins++;
            }
        }
        
        // should win most of the time
        assertTrue(wins >= 80, 
            "Expected >80 wins in 100 trials when near limit, got " + wins);
    }
}

package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VariableContributionStrategyTest {

    private final VariableContributionStrategy strategy = new VariableContributionStrategy();

    @Test
    void reducesContributionWhenPoolGrowsButKeepsMinimum() {
        Jackpot jackpot = new Jackpot();
        jackpot.setInitialPoolValue(new BigDecimal("5000"));
        jackpot.setCurrentPoolValue(new BigDecimal("5500"));
        jackpot.setContributionPercentage(new BigDecimal("10"));
        jackpot.setContributionDecayRate(new BigDecimal("0.01"));

        BigDecimal contribution = strategy.calculateContribution(jackpot, new BigDecimal("100"));

        assertThat(contribution).isEqualByComparingTo("9.95");
    }

    @Test
    void neverDropsBelowOnePercent() {
        Jackpot jackpot = new Jackpot();
        jackpot.setInitialPoolValue(new BigDecimal("100"));
        jackpot.setCurrentPoolValue(new BigDecimal("100000"));
        jackpot.setContributionPercentage(new BigDecimal("1.2"));
        jackpot.setContributionDecayRate(new BigDecimal("50"));

        BigDecimal contribution = strategy.calculateContribution(jackpot, new BigDecimal("100"));

        assertThat(contribution).isEqualByComparingTo("1.00");
    }
}

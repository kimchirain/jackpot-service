package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FixedContributionStrategyTest {

    private final FixedContributionStrategy strategy = new FixedContributionStrategy();

    @Test
    void calculatesFixedPercentageOfBetAmount() {
        Jackpot jackpot = new Jackpot();
        jackpot.setContributionPercentage(new BigDecimal("5"));

        BigDecimal contribution = strategy.calculateContribution(jackpot, new BigDecimal("200"));

        assertThat(contribution).isEqualByComparingTo("10.00");
    }
}

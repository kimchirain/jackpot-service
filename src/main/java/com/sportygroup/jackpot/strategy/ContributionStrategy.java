package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;

import java.math.BigDecimal;

public interface ContributionStrategy {
    BigDecimal calculateContribution(Jackpot jackpot, BigDecimal betAmount);
}

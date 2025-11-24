package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.entity.Jackpot;

public interface RewardStrategy {
    boolean determineWin(Jackpot jackpot);
}

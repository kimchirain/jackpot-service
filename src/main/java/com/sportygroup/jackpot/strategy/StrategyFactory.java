package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.enums.ContributionType;
import com.sportygroup.jackpot.enums.RewardType;
import org.springframework.stereotype.Component;

@Component
public class StrategyFactory {
    
    private final FixedContributionStrategy fixedContributionStrategy;
    private final VariableContributionStrategy variableContributionStrategy;
    private final FixedRewardStrategy fixedRewardStrategy;
    private final VariableRewardStrategy variableRewardStrategy;
    
    public StrategyFactory(FixedContributionStrategy fixedContributionStrategy,
                          VariableContributionStrategy variableContributionStrategy,
                          FixedRewardStrategy fixedRewardStrategy,
                          VariableRewardStrategy variableRewardStrategy) {
        this.fixedContributionStrategy = fixedContributionStrategy;
        this.variableContributionStrategy = variableContributionStrategy;
        this.fixedRewardStrategy = fixedRewardStrategy;
        this.variableRewardStrategy = variableRewardStrategy;
    }
    
    public ContributionStrategy getContributionStrategy(ContributionType type) {
        return switch (type) {
            case FIXED -> fixedContributionStrategy;
            case VARIABLE -> variableContributionStrategy;
        };
    }
    
    public RewardStrategy getRewardStrategy(RewardType type) {
        return switch (type) {
            case FIXED -> fixedRewardStrategy;
            case VARIABLE -> variableRewardStrategy;
        };
    }
}

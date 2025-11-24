package com.sportygroup.jackpot.strategy;

import com.sportygroup.jackpot.enums.ContributionType;
import com.sportygroup.jackpot.enums.RewardType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StrategyFactoryTest {

    private final StrategyFactory factory = new StrategyFactory(
            new FixedContributionStrategy(),
            new VariableContributionStrategy(),
            new FixedRewardStrategy(),
            new VariableRewardStrategy()
    );

    @Test
    void returnsContributionStrategyByType() {
        assertThat(factory.getContributionStrategy(ContributionType.FIXED))
                .isInstanceOf(FixedContributionStrategy.class);
        assertThat(factory.getContributionStrategy(ContributionType.VARIABLE))
                .isInstanceOf(VariableContributionStrategy.class);
    }

    @Test
    void returnsRewardStrategyByType() {
        assertThat(factory.getRewardStrategy(RewardType.FIXED))
                .isInstanceOf(FixedRewardStrategy.class);
        assertThat(factory.getRewardStrategy(RewardType.VARIABLE))
                .isInstanceOf(VariableRewardStrategy.class);
    }
}

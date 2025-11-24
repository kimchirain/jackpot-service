package com.sportygroup.jackpot.service;

import com.sportygroup.jackpot.dto.RewardResponse;
import com.sportygroup.jackpot.entity.Jackpot;
import com.sportygroup.jackpot.entity.JackpotContribution;
import com.sportygroup.jackpot.entity.JackpotReward;
import com.sportygroup.jackpot.enums.RewardType;
import com.sportygroup.jackpot.repository.JackpotContributionRepository;
import com.sportygroup.jackpot.repository.JackpotRepository;
import com.sportygroup.jackpot.repository.JackpotRewardRepository;
import com.sportygroup.jackpot.strategy.RewardStrategy;
import com.sportygroup.jackpot.strategy.StrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JackpotRewardServiceTest {

    @Mock
    private JackpotContributionRepository contributionRepository;
    @Mock
    private JackpotRewardRepository rewardRepository;
    @Mock
    private JackpotRepository jackpotRepository;
    @Mock
    private StrategyFactory strategyFactory;
    @Mock
    private RewardStrategy rewardStrategy;

    @InjectMocks
    private JackpotRewardService rewardService;

    @Test
    void returnsNotFoundWhenContributionMissing() {
        when(contributionRepository.findByBetId("BET-1")).thenReturn(Optional.empty());

        RewardResponse response = rewardService.evaluateReward("BET-1");

        assertThat(response).isEqualTo(RewardResponse.notFound());
        verifyNoInteractions(rewardRepository, jackpotRepository, strategyFactory);
    }

    @Test
    void shortCircuitsWhenRewardAlreadyExists() {
        JackpotContribution contribution = new JackpotContribution();
        contribution.setBetId("BET-2");
        when(contributionRepository.findByBetId("BET-2"))
                .thenReturn(Optional.of(contribution));
        when(rewardRepository.existsByBetId("BET-2")).thenReturn(true);

        RewardResponse response = rewardService.evaluateReward("BET-2");

        assertThat(response).isEqualTo(RewardResponse.noWin());
        verifyNoInteractions(jackpotRepository, strategyFactory);
    }

    @Test
    void returnsNoWinWhenStrategyDeclines() {
        JackpotContribution contribution = contribution("BET-3");
        Jackpot jackpot = jackpot();

        when(contributionRepository.findByBetId("BET-3"))
                .thenReturn(Optional.of(contribution));
        when(rewardRepository.existsByBetId("BET-3")).thenReturn(false);
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(strategyFactory.getRewardStrategy(RewardType.FIXED)).thenReturn(rewardStrategy);
        when(rewardStrategy.determineWin(jackpot)).thenReturn(false);

        RewardResponse response = rewardService.evaluateReward("BET-3");

        assertThat(response).isEqualTo(RewardResponse.noWin());
        verify(rewardRepository, never()).save(any(JackpotReward.class));
        assertThat(jackpot.getCurrentPoolValue()).isEqualByComparingTo("1500.00");
    }

    @Test
    void persistsRewardAndResetsJackpotWhenWinning() {
        JackpotContribution contribution = contribution("BET-4");
        Jackpot jackpot = jackpot();

        when(contributionRepository.findByBetId("BET-4"))
                .thenReturn(Optional.of(contribution));
        when(rewardRepository.existsByBetId("BET-4")).thenReturn(false);
        when(jackpotRepository.findById(1L)).thenReturn(Optional.of(jackpot));
        when(strategyFactory.getRewardStrategy(RewardType.FIXED)).thenReturn(rewardStrategy);
        when(rewardStrategy.determineWin(jackpot)).thenReturn(true);

        RewardResponse response = rewardService.evaluateReward("BET-4");

        ArgumentCaptor<JackpotReward> rewardCaptor = ArgumentCaptor.forClass(JackpotReward.class);
        verify(rewardRepository).save(rewardCaptor.capture());
        JackpotReward savedReward = rewardCaptor.getValue();

        assertThat(savedReward.getBetId()).isEqualTo("BET-4");
        assertThat(savedReward.getJackpotRewardAmount()).isEqualByComparingTo("1500.00");
        assertThat(response).isEqualTo(RewardResponse.winner(new BigDecimal("1500.00")));

        verify(jackpotRepository, atLeastOnce()).save(jackpot);
        assertThat(jackpot.getCurrentPoolValue()).isEqualByComparingTo(jackpot.getInitialPoolValue());
    }

    private JackpotContribution contribution(String betId) {
        JackpotContribution contribution = new JackpotContribution();
        contribution.setBetId(betId);
        contribution.setUserId("USER");
        contribution.setJackpotId(1L);
        return contribution;
    }

    private Jackpot jackpot() {
        Jackpot jackpot = new Jackpot();
        jackpot.setId(1L);
        jackpot.setInitialPoolValue(new BigDecimal("1000.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("1500.00"));
        jackpot.setRewardType(RewardType.FIXED);
        return jackpot;
    }
}

package com.sportygroup.jackpot.service;

import com.sportygroup.jackpot.dto.BetRequest;
import com.sportygroup.jackpot.entity.Jackpot;
import com.sportygroup.jackpot.entity.JackpotContribution;
import com.sportygroup.jackpot.enums.ContributionType;
import com.sportygroup.jackpot.repository.JackpotContributionRepository;
import com.sportygroup.jackpot.repository.JackpotRepository;
import com.sportygroup.jackpot.strategy.ContributionStrategy;
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
class JackpotContributionServiceTest {

    @Mock
    private JackpotRepository jackpotRepository;
    @Mock
    private JackpotContributionRepository contributionRepository;
    @Mock
    private StrategyFactory strategyFactory;
    @Mock
    private ContributionStrategy contributionStrategy;

    @InjectMocks
    private JackpotContributionService contributionService;

    @Test
    void skipsProcessingWhenBetAlreadyExists() {
        BetRequest betRequest = new BetRequest("BET-1", "USER", 1L, BigDecimal.TEN, "corr");
        when(contributionRepository.findByBetId("BET-1"))
                .thenReturn(Optional.of(new JackpotContribution()));

        contributionService.processBetContribution(betRequest);

        verify(contributionRepository, never()).save(any());
        verify(jackpotRepository, never()).findByIdWithLock(any());
    }

    @Test
    void persistsContributionAndUpdatesJackpot() {
        BetRequest betRequest = new BetRequest("BET-2", "USER", 1L, new BigDecimal("100.00"), "corr");
        Jackpot jackpot = new Jackpot();
        jackpot.setId(1L);
        jackpot.setInitialPoolValue(new BigDecimal("500.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("500.00"));
        jackpot.setContributionType(ContributionType.FIXED);

        when(contributionRepository.findByBetId("BET-2")).thenReturn(Optional.empty());
        when(jackpotRepository.findByIdWithLock(1L)).thenReturn(Optional.of(jackpot));
        when(strategyFactory.getContributionStrategy(any())).thenReturn(contributionStrategy);
        when(contributionStrategy.calculateContribution(jackpot, betRequest.betAmount()))
                .thenReturn(new BigDecimal("15.00"));

        contributionService.processBetContribution(betRequest);

        ArgumentCaptor<JackpotContribution> contributionCaptor = ArgumentCaptor.forClass(JackpotContribution.class);
        verify(contributionRepository).save(contributionCaptor.capture());
        JackpotContribution savedContribution = contributionCaptor.getValue();

        assertThat(savedContribution.getBetId()).isEqualTo("BET-2");
        assertThat(savedContribution.getContributionAmount()).isEqualByComparingTo("15.00");
        assertThat(savedContribution.getCurrentJackpotAmount()).isEqualByComparingTo("515.00");

        ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
        verify(jackpotRepository, atLeastOnce()).save(jackpotCaptor.capture());
        Jackpot updatedJackpot = jackpotCaptor.getValue();
        assertThat(updatedJackpot.getCurrentPoolValue()).isEqualByComparingTo("515.00");
    }
}

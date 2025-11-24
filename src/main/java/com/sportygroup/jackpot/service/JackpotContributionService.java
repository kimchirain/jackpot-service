package com.sportygroup.jackpot.service;

import com.sportygroup.jackpot.dto.BetRequest;
import com.sportygroup.jackpot.entity.Jackpot;
import com.sportygroup.jackpot.entity.JackpotContribution;
import com.sportygroup.jackpot.repository.JackpotContributionRepository;
import com.sportygroup.jackpot.repository.JackpotRepository;
import com.sportygroup.jackpot.strategy.ContributionStrategy;
import com.sportygroup.jackpot.strategy.StrategyFactory;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class JackpotContributionService {
    
    private static final Logger log = LoggerFactory.getLogger(JackpotContributionService.class);
    
    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository contributionRepository;
    private final StrategyFactory strategyFactory;
    
    public JackpotContributionService(JackpotRepository jackpotRepository,
                                     JackpotContributionRepository contributionRepository,
                                     StrategyFactory strategyFactory) {
        this.jackpotRepository = jackpotRepository;
        this.contributionRepository = contributionRepository;
        this.strategyFactory = strategyFactory;
    }
    
    @Transactional
    public void processBetContribution(BetRequest betRequest) {
        // set correlation ID for tracking across services
        if (betRequest.correlationId() != null) {
            MDC.put("correlationId", betRequest.correlationId());
        }
        MDC.put("betId", betRequest.betId());
        
        try {

            if (contributionRepository.findByBetId(betRequest.betId()).isPresent()) {
                log.warn("Bet {} already processed, skipping duplicate", betRequest.betId());
                return;
            }
            
            Jackpot jackpot = jackpotRepository.findByIdWithLock(betRequest.jackpotId())
                    .orElseThrow(() -> new RuntimeException("Jackpot not found: " + betRequest.jackpotId()));
            
            // calculate contribution
            ContributionStrategy strategy = strategyFactory.getContributionStrategy(jackpot.getContributionType());
            BigDecimal contributionAmount = strategy.calculateContribution(jackpot, betRequest.betAmount());
            
            // update jackpot pool
            BigDecimal newPoolValue = jackpot.getCurrentPoolValue().add(contributionAmount);
            jackpot.setCurrentPoolValue(newPoolValue);
            jackpotRepository.save(jackpot);
            
            // save contribution
            JackpotContribution contribution = new JackpotContribution();
            contribution.setBetId(betRequest.betId());
            contribution.setUserId(betRequest.userId());
            contribution.setJackpotId(betRequest.jackpotId());
            contribution.setStakeAmount(betRequest.betAmount());
            contribution.setContributionAmount(contributionAmount);
            contribution.setCurrentJackpotAmount(newPoolValue);
            
            contributionRepository.save(contribution);
            
            log.info("Bet {} contributed {} to jackpot {}. New pool: {}", 
                    betRequest.betId(), contributionAmount, jackpot.getId(), newPoolValue);
        } finally {
            MDC.clear();
        }
    }
}

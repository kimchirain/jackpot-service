package com.sportygroup.jackpot.service;

import com.sportygroup.jackpot.dto.RewardResponse;
import com.sportygroup.jackpot.entity.Jackpot;
import com.sportygroup.jackpot.entity.JackpotContribution;
import com.sportygroup.jackpot.entity.JackpotReward;
import com.sportygroup.jackpot.repository.JackpotContributionRepository;
import com.sportygroup.jackpot.repository.JackpotRepository;
import com.sportygroup.jackpot.repository.JackpotRewardRepository;
import com.sportygroup.jackpot.strategy.RewardStrategy;
import com.sportygroup.jackpot.strategy.StrategyFactory;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JackpotRewardService {
    
    private static final Logger log = LoggerFactory.getLogger(JackpotRewardService.class);
    
    private final JackpotContributionRepository contributionRepository;
    private final JackpotRewardRepository rewardRepository;
    private final JackpotRepository jackpotRepository;
    private final StrategyFactory strategyFactory;
    
    public JackpotRewardService(JackpotContributionRepository contributionRepository,
                                JackpotRewardRepository rewardRepository,
                                JackpotRepository jackpotRepository,
                                StrategyFactory strategyFactory) {
        this.contributionRepository = contributionRepository;
        this.rewardRepository = rewardRepository;
        this.jackpotRepository = jackpotRepository;
        this.strategyFactory = strategyFactory;
    }
    
    @Transactional
    public RewardResponse evaluateReward(String betId) {
        // check if bet exists and already contributed
        JackpotContribution contribution = contributionRepository.findByBetId(betId)
                .orElse(null);
        
        if (contribution == null) {
            log.warn("Bet not found or not contributed: {}", betId);
            return RewardResponse.notFound();
        }
        
        // check if already rewarded
        if (rewardRepository.existsByBetId(betId)) {
            log.info("Bet {} already rewarded", betId);
            return RewardResponse.noWin();
        }
        
        Jackpot jackpot = jackpotRepository.findById(contribution.getJackpotId())
                .orElseThrow(() -> new RuntimeException("Jackpot not found"));
        
        RewardStrategy strategy = strategyFactory.getRewardStrategy(jackpot.getRewardType());
        boolean won = strategy.determineWin(jackpot);
        
        if (!won) {
            log.info("Bet {} did not win jackpot {}", betId, jackpot.getId());
            return RewardResponse.noWin();
        }
        
        return processWin(contribution, jackpot);
    }
    
    private RewardResponse processWin(JackpotContribution contribution, Jackpot jackpot) {

        JackpotReward reward = new JackpotReward();
        reward.setBetId(contribution.getBetId());
        reward.setUserId(contribution.getUserId());
        reward.setJackpotId(jackpot.getId());
        reward.setJackpotRewardAmount(jackpot.getCurrentPoolValue());
        
        rewardRepository.save(reward);
        
        // if won reset jackpot pool
        jackpot.setCurrentPoolValue(jackpot.getInitialPoolValue());
        jackpotRepository.save(jackpot);
        
        log.info("Bet {} WON jackpot {}! Reward: {}", 
                contribution.getBetId(), jackpot.getId(), reward.getJackpotRewardAmount());
        
        return RewardResponse.winner(reward.getJackpotRewardAmount());
    }
}

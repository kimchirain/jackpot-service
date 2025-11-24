package com.sportygroup.jackpot.config;

import com.sportygroup.jackpot.entity.Jackpot;
import com.sportygroup.jackpot.enums.ContributionType;
import com.sportygroup.jackpot.enums.RewardType;
import com.sportygroup.jackpot.repository.JackpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final JackpotRepository jackpotRepository;
    
    public DataInitializer(JackpotRepository jackpotRepository) {
        this.jackpotRepository = jackpotRepository;
    }
    
    @Override
    public void run(String... args) {
        // Create sample jackpots
        createFixedJackpot();
        createVariableJackpot();
        log.info("Sample jackpots initialized");
    }
    
    private void createFixedJackpot() {
        Jackpot jackpot = new Jackpot();
        jackpot.setInitialPoolValue(new BigDecimal("10000.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("10000.00"));
        jackpot.setContributionType(ContributionType.FIXED);
        jackpot.setContributionPercentage(new BigDecimal("5.0")); // 5% of bet
        jackpot.setRewardType(RewardType.FIXED);
        jackpot.setRewardPercentage(new BigDecimal("1.0")); // 1% chance to win
        
        jackpotRepository.save(jackpot);
        log.info("Created Fixed Jackpot with ID: {}", jackpot.getId());
    }
    
    private void createVariableJackpot() {
        Jackpot jackpot = new Jackpot();
        jackpot.setInitialPoolValue(new BigDecimal("5000.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("5000.00"));
        jackpot.setContributionType(ContributionType.VARIABLE);
        jackpot.setContributionPercentage(new BigDecimal("10.0")); // Starting at 10%
        jackpot.setContributionDecayRate(new BigDecimal("0.01")); // Decays as pool grows
        jackpot.setRewardType(RewardType.VARIABLE);
        jackpot.setRewardPercentage(new BigDecimal("0.1")); // Starting at 0.1%
        jackpot.setRewardPoolLimit(new BigDecimal("50000.00")); // 100% chance at 50k
        
        jackpotRepository.save(jackpot);
        log.info("Created Variable Jackpot with ID: {}", jackpot.getId());
    }
}

package com.sportygroup.jackpot.config;

import com.sportygroup.jackpot.dto.BetRequest;
import com.sportygroup.jackpot.service.JackpotContributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jackpot.mock.kafka", havingValue = "true")
public class MockKafkaConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(MockKafkaConsumer.class);
    
    private final JackpotContributionService contributionService;
    
    public MockKafkaConsumer(JackpotContributionService contributionService) {
        this.contributionService = contributionService;
    }
    
    @EventListener
    public void handleBetPublished(BetPublishedEvent event) {
        BetRequest betRequest = event.getBetRequest();
        log.info("MOCK KAFKA CONSUMER - Consuming bet: {}", betRequest);
        
        try {
            contributionService.processBetContribution(betRequest);
            log.info("MOCK KAFKA CONSUMER - Successfully processed bet: {}", betRequest.betId());
        } catch (Exception e) {
            log.error("MOCK KAFKA CONSUMER - Error processing bet: {}", betRequest.betId(), e);
        }
    }
}

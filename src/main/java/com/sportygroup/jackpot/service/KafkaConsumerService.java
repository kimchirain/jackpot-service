package com.sportygroup.jackpot.service;

import com.sportygroup.jackpot.dto.BetRequest;
import com.sportygroup.jackpot.dto.FailedBetMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jackpot.mock.kafka", havingValue = "false", matchIfMissing = false)
public class KafkaConsumerService {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    private final JackpotContributionService contributionService;
    private final KafkaTemplate<String, FailedBetMessage> kafkaTemplate;
    
    @Value("${jackpot.dlq.topic.name}")
    private String dlqTopicName;
    
    @Value("${jackpot.max.retry.attempts}")
    private int maxRetryAttempts;
    
    public KafkaConsumerService(JackpotContributionService contributionService,
                                KafkaTemplate<String, FailedBetMessage> kafkaTemplate) {
        this.contributionService = contributionService;
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @KafkaListener(topics = "${jackpot.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBet(BetRequest betRequest,
                          @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
                          @Header(value = KafkaHeaders.OFFSET, required = false) Long offset,
                          @Header(value = "retry-count", required = false, defaultValue = "0") Integer retryCount) {
        
        log.info("Received bet from Kafka [partition={}, offset={}]: {}", partition, offset, betRequest);
        
        try {
            contributionService.processBetContribution(betRequest);
            log.info("Successfully processed bet: {}", betRequest.betId());
            
        } catch (Exception e) {
            log.error("Error processing bet: {} (attempt {}/{})", 
                betRequest.betId(), retryCount + 1, maxRetryAttempts, e);
            
            handleFailedBet(betRequest, e, retryCount);
        }
    }
    
    private void handleFailedBet(BetRequest betRequest, Exception e, int retryCount) {
        retryCount++;
        
        if (retryCount < maxRetryAttempts) {
            // Retry with exponential backoff
            log.warn("Bet {} failed, will retry (attempt {}/{})", 
                betRequest.betId(), retryCount, maxRetryAttempts);
            
            try {
                Thread.sleep(calculateBackoffDelay(retryCount));
                // here would be republish to retry topic
                contributionService.processBetContribution(betRequest);
                log.info("Retry successful for bet: {}", betRequest.betId());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("Retry interrupted for bet: {}", betRequest.betId(), ie);
                sendToDeadLetterQueue(betRequest, e, retryCount);
            } catch (Exception retryException) {
                log.error("Retry failed for bet: {}", betRequest.betId(), retryException);
                if (retryCount >= maxRetryAttempts - 1) {
                    sendToDeadLetterQueue(betRequest, retryException, retryCount);
                }
            }
        } else {
            // if max retries exceeded, send to DLQ
            sendToDeadLetterQueue(betRequest, e, retryCount);
        }
    }
    
    private void sendToDeadLetterQueue(BetRequest betRequest, Exception e, int retryCount) {
        log.error("Max retries exceeded for bet {}. Sending to DLQ: {}", 
            betRequest.betId(), dlqTopicName);
        
        try {
            FailedBetMessage failedMessage = FailedBetMessage.from(betRequest, e, retryCount);
            kafkaTemplate.send(dlqTopicName, betRequest.betId(), failedMessage);
            log.info("Bet {} sent to DLQ successfully", betRequest.betId());
        } catch (Exception dlqException) {
            // if DLQ itself fails, here we would trigger alert/monitoring
            log.error("CRITICAL: Failed to send bet {} to DLQ!", betRequest.betId(), dlqException);
        }
    }
    
    private long calculateBackoffDelay(int retryCount) {
        // exponential backoff
        return (long) Math.pow(2, retryCount - 1) * 1000;
    }
}

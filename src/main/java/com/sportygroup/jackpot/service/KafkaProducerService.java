package com.sportygroup.jackpot.service;

import com.sportygroup.jackpot.config.BetPublishedEvent;
import com.sportygroup.jackpot.dto.BetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    
    private final KafkaTemplate<String, BetRequest> kafkaTemplate;
    private final ApplicationEventPublisher eventPublisher;
    
    @Value("${jackpot.topic.name}")
    private String topicName;
    
    @Value("${jackpot.mock.kafka}")
    private boolean mockKafka;
    
    public KafkaProducerService(KafkaTemplate<String, BetRequest> kafkaTemplate,
                                ApplicationEventPublisher eventPublisher) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventPublisher = eventPublisher;
    }
    
    public void publishBet(BetRequest betRequest) {
        if (mockKafka) {
            log.info("Publishing bet to mock Kafka: {}", betRequest.betId());
            eventPublisher.publishEvent(new BetPublishedEvent(this, betRequest));
        } else {
            log.info("Publishing bet to Kafka topic {}: {}", topicName, betRequest.betId());
            kafkaTemplate.send(topicName, betRequest.betId(), betRequest);
        }
    }
}

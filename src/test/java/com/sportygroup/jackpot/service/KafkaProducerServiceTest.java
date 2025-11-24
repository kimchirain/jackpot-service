package com.sportygroup.jackpot.service;

import com.sportygroup.jackpot.config.BetPublishedEvent;
import com.sportygroup.jackpot.dto.BetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaProducerServiceTest {

    private KafkaTemplate<String, BetRequest> kafkaTemplate;
    private ApplicationEventPublisher eventPublisher;
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        kafkaProducerService = new KafkaProducerService(kafkaTemplate, eventPublisher);
        ReflectionTestUtils.setField(kafkaProducerService, "topicName", "jackpot-bets");
    }

    @Test
    void publishesEventWhenMockKafkaEnabled() {
        ReflectionTestUtils.setField(kafkaProducerService, "mockKafka", true);
        BetRequest betRequest = new BetRequest("BET", "USER", 1L, BigDecimal.TEN, "corr");

        kafkaProducerService.publishBet(betRequest);

        ArgumentCaptor<BetPublishedEvent> eventCaptor = ArgumentCaptor.forClass(BetPublishedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getBetRequest()).isEqualTo(betRequest);
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void sendsToKafkaTopicWhenMockDisabled() {
        ReflectionTestUtils.setField(kafkaProducerService, "mockKafka", false);
        BetRequest betRequest = new BetRequest("BET", "USER", 1L, BigDecimal.TEN, "corr");

        kafkaProducerService.publishBet(betRequest);

        verify(kafkaTemplate).send("jackpot-bets", "BET", betRequest);
        verify(eventPublisher, never()).publishEvent(any());
    }
}

package com.sportygroup.jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.jackpot.dto.BetRequest;
import com.sportygroup.jackpot.dto.RewardResponse;
import com.sportygroup.jackpot.service.JackpotRewardService;
import com.sportygroup.jackpot.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JackpotControllerTest {

    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private JackpotRewardService rewardService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        JackpotController controller = new JackpotController(kafkaProducerService, rewardService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void publishesBetRequests() throws Exception {
        BetRequest betRequest = new BetRequest("BET-123", "USER-1", 1L, new BigDecimal("50.00"), "corr");

        mockMvc.perform(post("/api/jackpot/bets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(betRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Bet published successfully"));

        ArgumentCaptor<BetRequest> betCaptor = ArgumentCaptor.forClass(BetRequest.class);
        verify(kafkaProducerService).publishBet(betCaptor.capture());
        assertThat(betCaptor.getValue()).isEqualTo(betRequest);
    }

    @Test
    void returnsRewardEvaluations() throws Exception {
        RewardResponse response = RewardResponse.winner(new BigDecimal("100.00"));
        when(rewardService.evaluateReward(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/jackpot/rewards/BET-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.won").value(true))
                .andExpect(jsonPath("$.rewardAmount").value(100.00));

        verify(rewardService).evaluateReward("BET-1");
    }

    @Test
    void exposesHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/jackpot/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Jackpot Service is running"));
    }
}

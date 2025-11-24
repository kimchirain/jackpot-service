package com.sportygroup.jackpot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.jackpot.dto.BetRequest;
import com.sportygroup.jackpot.entity.Jackpot;
import com.sportygroup.jackpot.entity.JackpotContribution;
import com.sportygroup.jackpot.enums.ContributionType;
import com.sportygroup.jackpot.enums.RewardType;
import com.sportygroup.jackpot.repository.JackpotContributionRepository;
import com.sportygroup.jackpot.repository.JackpotRepository;
import com.sportygroup.jackpot.repository.JackpotRewardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JackpotIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JackpotRepository jackpotRepository;

    @Autowired
    private JackpotContributionRepository contributionRepository;

    @Autowired
    private JackpotRewardRepository rewardRepository;

    private Jackpot testJackpot;

    @BeforeEach
    void setUp() {
        // clean up
        rewardRepository.deleteAll();
        contributionRepository.deleteAll();
        jackpotRepository.deleteAll();

        // create test jackpot
        testJackpot = new Jackpot();
        testJackpot.setInitialPoolValue(new BigDecimal("1000.00"));
        testJackpot.setCurrentPoolValue(new BigDecimal("1000.00"));
        testJackpot.setContributionType(ContributionType.FIXED);
        testJackpot.setContributionPercentage(new BigDecimal("10.0"));
        testJackpot.setRewardType(RewardType.FIXED);
        testJackpot.setRewardPercentage(new BigDecimal("100.0")); // always win for testing
        testJackpot = jackpotRepository.save(testJackpot);
    }

    @Test
    void testFullBetFlow_PublishContributeAndReward() throws Exception {
        // Step 1: publish a bet
        BetRequest betRequest = new BetRequest(
            "INT-BET-001",
            "INT-USER-001",
            testJackpot.getId(),
            new BigDecimal("100.00"),
            null
        );

        mockMvc.perform(post("/api/jackpot/bets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(betRequest)))
                .andExpect(status().isOk());

        // async processing
        Thread.sleep(500);

        // Step 2: verify contribution was recorded
        Optional<JackpotContribution> contribution = 
            contributionRepository.findByBetId("INT-BET-001");
        
        assertTrue(contribution.isPresent(), "Contribution should be recorded");
        assertEquals("INT-USER-001", contribution.get().getUserId());
        assertEquals(new BigDecimal("100.00"), contribution.get().getStakeAmount());
        assertEquals(new BigDecimal("10.00"), contribution.get().getContributionAmount());

        // Step 3: verify jackpot pool updated
        Jackpot updatedJackpot = jackpotRepository.findById(testJackpot.getId()).orElseThrow();
        assertEquals(new BigDecimal("1010.00"), updatedJackpot.getCurrentPoolValue());

        // Step 4: evaluate reward
        mockMvc.perform(get("/api/jackpot/rewards/INT-BET-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.won").value(true))
                .andExpect(jsonPath("$.rewardAmount").value(1010.00));

        // Step 5: verify jackpot was reset
        Jackpot resetJackpot = jackpotRepository.findById(testJackpot.getId()).orElseThrow();
        assertEquals(new BigDecimal("1000.00"), resetJackpot.getCurrentPoolValue());

        // Step 6: verify reward was recorded
        assertTrue(rewardRepository.existsByBetId("INT-BET-001"));
    }

    @Test
    void testMultipleBets_PoolAccumulation() throws Exception {
        // place 3 bets
        for (int i = 1; i <= 3; i++) {
            BetRequest betRequest = new BetRequest(
                "MULTI-BET-" + i,
                "USER-" + i,
                testJackpot.getId(),
                new BigDecimal("50.00"),
                null
            );

            mockMvc.perform(post("/api/jackpot/bets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(betRequest)))
                    .andExpect(status().isOk());

            Thread.sleep(200);
        }

        // 3 bets * 50 * 10% = 15 contribution
        // Pool should be 1000 + 15 = 1015
        Jackpot updatedJackpot = jackpotRepository.findById(testJackpot.getId()).orElseThrow();
        assertEquals(new BigDecimal("1015.00"), updatedJackpot.getCurrentPoolValue());

        // Verify all contributions recorded
        assertEquals(3, contributionRepository.count());
    }

    @Test
    void testEvaluateReward_BeforeContribution() throws Exception {
        // evaluate reward for non-existent bet
        mockMvc.perform(get("/api/jackpot/rewards/NON-EXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.won").value(false))
                .andExpect(jsonPath("$.message")
                    .value("Bet not found or not eligible for reward"));
    }

    @Test
    void testInvalidBet_ValidationFailure() throws Exception {
        // missing required field
        String invalidJson = """
            {
                "betId": "INVALID-001",
                "jackpotId": 1,
                "betAmount": 100.00
            }
            """;

        mockMvc.perform(post("/api/jackpot/bets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        // verify no contribution recorded
        assertEquals(0, contributionRepository.count());
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/jackpot/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Jackpot Service is running"));
    }
}

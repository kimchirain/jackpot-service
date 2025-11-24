package com.sportygroup.jackpot.controller;

import com.sportygroup.jackpot.dto.BetRequest;
import com.sportygroup.jackpot.dto.RewardResponse;
import com.sportygroup.jackpot.service.JackpotRewardService;
import com.sportygroup.jackpot.service.KafkaProducerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jackpot")
public class JackpotController {
    
    private static final Logger log = LoggerFactory.getLogger(JackpotController.class);
    
    private final KafkaProducerService kafkaProducerService;
    private final JackpotRewardService rewardService;
    
    public JackpotController(KafkaProducerService kafkaProducerService, JackpotRewardService rewardService) {
        this.kafkaProducerService = kafkaProducerService;
        this.rewardService = rewardService;
    }
    
    @PostMapping("/bets")
    public ResponseEntity<String> publishBet(@Valid @RequestBody BetRequest betRequest) {

        String correlationId = betRequest.correlationId() != null 
            ? betRequest.correlationId() 
            : UUID.randomUUID().toString();
        
        if (betRequest.correlationId() == null) {
            betRequest = new BetRequest(
                betRequest.betId(),
                betRequest.userId(),
                betRequest.jackpotId(),
                betRequest.betAmount(),
                correlationId
            );
        }
        
        MDC.put("correlationId", correlationId);
        MDC.put("betId", betRequest.betId());
        
        try {
            log.info("Received bet request: {}", betRequest);
            kafkaProducerService.publishBet(betRequest);
            return ResponseEntity.ok("Bet published successfully");
        } finally {
            MDC.clear();
        }
    }
    
    @GetMapping("/rewards/{betId}")
    public ResponseEntity<RewardResponse> evaluateReward(@PathVariable String betId) {
        MDC.put("betId", betId);
        try {
            log.info("Evaluating reward for bet: {}", betId);
            RewardResponse response = rewardService.evaluateReward(betId);
            return ResponseEntity.ok(response);
        } finally {
            MDC.clear();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Jackpot Service is running");
    }
}

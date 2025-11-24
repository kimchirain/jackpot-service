package com.sportygroup.jackpot.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jackpot_rewards")
public class JackpotReward {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String betId;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private Long jackpotId;
    
    @Column(nullable = false)
    private BigDecimal jackpotRewardAmount;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public JackpotReward() {
    }
    
    public JackpotReward(Long id, String betId, String userId, Long jackpotId,
                         BigDecimal jackpotRewardAmount, LocalDateTime createdAt) {
        this.id = id;
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.jackpotRewardAmount = jackpotRewardAmount;
        this.createdAt = createdAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBetId() {
        return betId;
    }
    
    public void setBetId(String betId) {
        this.betId = betId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Long getJackpotId() {
        return jackpotId;
    }
    
    public void setJackpotId(Long jackpotId) {
        this.jackpotId = jackpotId;
    }
    
    public BigDecimal getJackpotRewardAmount() {
        return jackpotRewardAmount;
    }
    
    public void setJackpotRewardAmount(BigDecimal jackpotRewardAmount) {
        this.jackpotRewardAmount = jackpotRewardAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

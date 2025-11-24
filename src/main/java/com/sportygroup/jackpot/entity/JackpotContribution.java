package com.sportygroup.jackpot.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jackpot_contributions")
public class JackpotContribution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String betId;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private Long jackpotId;
    
    @Column(nullable = false)
    private BigDecimal stakeAmount;
    
    @Column(nullable = false)
    private BigDecimal contributionAmount;
    
    @Column(nullable = false)
    private BigDecimal currentJackpotAmount;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public JackpotContribution() {
    }
    
    public JackpotContribution(Long id, String betId, String userId, Long jackpotId,
                               BigDecimal stakeAmount, BigDecimal contributionAmount,
                               BigDecimal currentJackpotAmount, LocalDateTime createdAt) {
        this.id = id;
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.stakeAmount = stakeAmount;
        this.contributionAmount = contributionAmount;
        this.currentJackpotAmount = currentJackpotAmount;
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
    
    public BigDecimal getStakeAmount() {
        return stakeAmount;
    }
    
    public void setStakeAmount(BigDecimal stakeAmount) {
        this.stakeAmount = stakeAmount;
    }
    
    public BigDecimal getContributionAmount() {
        return contributionAmount;
    }
    
    public void setContributionAmount(BigDecimal contributionAmount) {
        this.contributionAmount = contributionAmount;
    }
    
    public BigDecimal getCurrentJackpotAmount() {
        return currentJackpotAmount;
    }
    
    public void setCurrentJackpotAmount(BigDecimal currentJackpotAmount) {
        this.currentJackpotAmount = currentJackpotAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

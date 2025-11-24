package com.sportygroup.jackpot.entity;

import com.sportygroup.jackpot.enums.ContributionType;
import com.sportygroup.jackpot.enums.RewardType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "jackpots")
public class Jackpot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private BigDecimal initialPoolValue;
    
    @Column(nullable = false)
    private BigDecimal currentPoolValue;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContributionType contributionType;
    
    @Column(nullable = false)
    private BigDecimal contributionPercentage;
    
    @Column
    private BigDecimal contributionDecayRate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType;
    
    @Column(nullable = false)
    private BigDecimal rewardPercentage;
    
    @Column
    private BigDecimal rewardPoolLimit; //when pool hits this, chance becomes 100%
    
    @Version
    private Long version; 
    
    public Jackpot() {
    }
    
    public Jackpot(Long id, BigDecimal initialPoolValue, BigDecimal currentPoolValue,
                   ContributionType contributionType, BigDecimal contributionPercentage,
                   BigDecimal contributionDecayRate, RewardType rewardType,
                   BigDecimal rewardPercentage, BigDecimal rewardPoolLimit, Long version) {
        this.id = id;
        this.initialPoolValue = initialPoolValue;
        this.currentPoolValue = currentPoolValue;
        this.contributionType = contributionType;
        this.contributionPercentage = contributionPercentage;
        this.contributionDecayRate = contributionDecayRate;
        this.rewardType = rewardType;
        this.rewardPercentage = rewardPercentage;
        this.rewardPoolLimit = rewardPoolLimit;
        this.version = version;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getInitialPoolValue() {
        return initialPoolValue;
    }
    
    public void setInitialPoolValue(BigDecimal initialPoolValue) {
        this.initialPoolValue = initialPoolValue;
    }
    
    public BigDecimal getCurrentPoolValue() {
        return currentPoolValue;
    }
    
    public void setCurrentPoolValue(BigDecimal currentPoolValue) {
        this.currentPoolValue = currentPoolValue;
    }
    
    public ContributionType getContributionType() {
        return contributionType;
    }
    
    public void setContributionType(ContributionType contributionType) {
        this.contributionType = contributionType;
    }
    
    public BigDecimal getContributionPercentage() {
        return contributionPercentage;
    }
    
    public void setContributionPercentage(BigDecimal contributionPercentage) {
        this.contributionPercentage = contributionPercentage;
    }
    
    public BigDecimal getContributionDecayRate() {
        return contributionDecayRate;
    }
    
    public void setContributionDecayRate(BigDecimal contributionDecayRate) {
        this.contributionDecayRate = contributionDecayRate;
    }
    
    public RewardType getRewardType() {
        return rewardType;
    }
    
    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }
    
    public BigDecimal getRewardPercentage() {
        return rewardPercentage;
    }
    
    public void setRewardPercentage(BigDecimal rewardPercentage) {
        this.rewardPercentage = rewardPercentage;
    }
    
    public BigDecimal getRewardPoolLimit() {
        return rewardPoolLimit;
    }
    
    public void setRewardPoolLimit(BigDecimal rewardPoolLimit) {
        this.rewardPoolLimit = rewardPoolLimit;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
}

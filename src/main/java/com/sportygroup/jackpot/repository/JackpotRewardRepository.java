package com.sportygroup.jackpot.repository;

import com.sportygroup.jackpot.entity.JackpotReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JackpotRewardRepository extends JpaRepository<JackpotReward, Long> {
    boolean existsByBetId(String betId);
}

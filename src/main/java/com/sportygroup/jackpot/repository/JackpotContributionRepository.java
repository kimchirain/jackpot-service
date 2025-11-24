package com.sportygroup.jackpot.repository;

import com.sportygroup.jackpot.entity.JackpotContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotContributionRepository extends JpaRepository<JackpotContribution, Long> {
    Optional<JackpotContribution> findByBetId(String betId);
}

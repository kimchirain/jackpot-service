package com.sportygroup.jackpot.repository;

import com.sportygroup.jackpot.entity.Jackpot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotRepository extends JpaRepository<Jackpot, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Jackpot j WHERE j.id = :id")
    Optional<Jackpot> findByIdWithLock(@Param("id") Long id);
}

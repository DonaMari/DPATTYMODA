package com.dpatty.repository;

import com.dpatty.model.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {
    Optional<GiftCard> findByCodeAndIsActiveTrue(String code);
    
    @Query("SELECT g FROM GiftCard g WHERE g.code = :code AND g.isActive = true AND g.currentBalance > 0 AND (g.expiresAt IS NULL OR g.expiresAt > :now)")
    Optional<GiftCard> findValidGiftCard(@Param("code") String code, @Param("now") LocalDateTime now);
    
    boolean existsByCode(String code);
}
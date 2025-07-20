package com.dpatty.repository;

import com.dpatty.model.CashSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashSessionRepository extends JpaRepository<CashSession, Long> {
    Optional<CashSession> findByCashierIdAndIsActiveTrue(Long cashierId);
    
    @Query("SELECT cs FROM CashSession cs WHERE cs.store.id = :storeId AND cs.isActive = true")
    List<CashSession> findActiveSessionsByStore(@Param("storeId") Long storeId);
    
    @Query("SELECT cs FROM CashSession cs WHERE cs.openedAt BETWEEN :startDate AND :endDate ORDER BY cs.openedAt DESC")
    Page<CashSession> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate, 
                                     Pageable pageable);
    
    boolean existsBySessionNumber(String sessionNumber);
}
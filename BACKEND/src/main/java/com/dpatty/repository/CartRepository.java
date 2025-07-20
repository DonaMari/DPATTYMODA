package com.dpatty.repository;

import com.dpatty.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    Optional<Cart> findBySessionId(String sessionId);
    
    @Query("SELECT c FROM Cart c WHERE c.updatedAt < :cutoffDate AND c.user IS NULL")
    List<Cart> findAbandonedGuestCarts(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT c FROM Cart c JOIN FETCH c.items ci JOIN FETCH ci.productVariant pv JOIN FETCH pv.product WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);
}
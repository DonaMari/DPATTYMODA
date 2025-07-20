package com.dpatty.repository;

import com.dpatty.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId OR m.recipient.id = :userId) ORDER BY m.createdAt DESC")
    Page<Message> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE ((m.sender.id = :senderId AND m.recipient.id = :recipientId) OR (m.sender.id = :recipientId AND m.recipient.id = :senderId)) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient.id = :userId AND m.isRead = false")
    Long countUnreadMessages(@Param("userId") Long userId);
    
    @Query("SELECT m FROM Message m WHERE m.recipient.id = :userId AND m.isRead = false")
    List<Message> findUnreadMessages(@Param("userId") Long userId);
}
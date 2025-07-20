package com.dpatty.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_sessions")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CashSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "session_number", unique = true, nullable = false)
    private String sessionNumber;

    @Column(name = "opening_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal openingAmount;

    @Column(name = "closing_amount", precision = 10, scale = 2)
    private BigDecimal closingAmount;

    @Column(name = "expected_amount", precision = 10, scale = 2)
    private BigDecimal expectedAmount;

    @Column(name = "cash_sales_amount", precision = 10, scale = 2)
    private BigDecimal cashSalesAmount = BigDecimal.ZERO;

    @Column(name = "card_sales_amount", precision = 10, scale = 2)
    private BigDecimal cardSalesAmount = BigDecimal.ZERO;

    @Column(name = "digital_sales_amount", precision = 10, scale = 2)
    private BigDecimal digitalSalesAmount = BigDecimal.ZERO;

    @Column(name = "total_sales_amount", precision = 10, scale = 2)
    private BigDecimal totalSalesAmount = BigDecimal.ZERO;

    @Column(name = "expenses_amount", precision = 10, scale = 2)
    private BigDecimal expensesAmount = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
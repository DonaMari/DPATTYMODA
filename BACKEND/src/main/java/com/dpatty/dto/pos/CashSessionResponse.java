package com.dpatty.dto.pos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CashSessionResponse {
    private Long id;
    private String sessionNumber;
    private String cashierName;
    private String storeName;
    private BigDecimal openingAmount;
    private BigDecimal closingAmount;
    private BigDecimal expectedAmount;
    private BigDecimal cashSalesAmount;
    private BigDecimal cardSalesAmount;
    private BigDecimal digitalSalesAmount;
    private BigDecimal totalSalesAmount;
    private BigDecimal expensesAmount;
    private Boolean isActive;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private String notes;
}
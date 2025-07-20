package com.dpatty.dto.report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopProductResponse {
    private Long productId;
    private String productName;
    private String productSku;
    private Long totalSold;
    private BigDecimal totalRevenue;
}
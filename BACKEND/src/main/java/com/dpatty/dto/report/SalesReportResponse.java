package com.dpatty.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SalesReportResponse {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private Map<String, BigDecimal> salesByDay;
    private Map<String, BigDecimal> salesByOrderType;
}
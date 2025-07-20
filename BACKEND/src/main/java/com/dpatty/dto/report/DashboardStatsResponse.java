package com.dpatty.dto.report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardStatsResponse {
    // Today's stats
    private Long todayOrders;
    private BigDecimal todayRevenue;
    
    // Monthly stats
    private Long monthlyOrders;
    private BigDecimal monthlyRevenue;
    
    // Yearly stats
    private Long yearlyOrders;
    private BigDecimal yearlyRevenue;
    
    // Total stats
    private Long totalProducts;
    private Long totalUsers;
    private Long totalOrders;
}
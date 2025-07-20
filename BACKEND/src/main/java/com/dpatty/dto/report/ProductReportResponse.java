package com.dpatty.dto.report;

import lombok.Data;

@Data
public class ProductReportResponse {
    private Long totalProducts;
    private Long activeProducts;
    private Long inactiveProducts;
    private Long lowStockProducts;
}
package com.dpatty.dto.report;

import lombok.Data;

@Data
public class CustomerReportResponse {
    private Long totalCustomers;
    private Long newCustomersThisMonth;
    private Long activeCustomers;
}
package com.dpatty.controller;

import com.dpatty.dto.report.*;
import com.dpatty.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
@Tag(name = "Reports", description = "Reportes y estadísticas")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Obtener estadísticas del dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = reportService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/sales")
    @Operation(summary = "Obtener reporte de ventas")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        SalesReportResponse report = reportService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/products")
    @Operation(summary = "Obtener reporte de productos")
    public ResponseEntity<ProductReportResponse> getProductReport() {
        ProductReportResponse report = reportService.getProductReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/top-products")
    @Operation(summary = "Obtener productos más vendidos")
    public ResponseEntity<List<TopProductResponse>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<TopProductResponse> products = reportService.getTopSellingProducts(limit);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/customers")
    @Operation(summary = "Obtener reporte de clientes")
    public ResponseEntity<CustomerReportResponse> getCustomerReport() {
        CustomerReportResponse report = reportService.getCustomerReport();
        return ResponseEntity.ok(report);
    }
}
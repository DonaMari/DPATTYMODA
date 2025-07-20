package com.dpatty.service;

import com.dpatty.dto.report.*;
import com.dpatty.model.Order;
import com.dpatty.repository.OrderRepository;
import com.dpatty.repository.ProductRepository;
import com.dpatty.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardStatsResponse getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startOfMonth = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startOfYear = now.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);

        DashboardStatsResponse stats = new DashboardStatsResponse();
        
        // Today's stats
        stats.setTodayOrders(orderRepository.countOrdersSince(startOfDay));
        stats.setTodayRevenue(BigDecimal.valueOf(orderRepository.getTotalRevenueSince(startOfDay) != null ? 
            orderRepository.getTotalRevenueSince(startOfDay) : 0.0));
        
        // Monthly stats
        stats.setMonthlyOrders(orderRepository.countOrdersSince(startOfMonth));
        stats.setMonthlyRevenue(BigDecimal.valueOf(orderRepository.getTotalRevenueSince(startOfMonth) != null ? 
            orderRepository.getTotalRevenueSince(startOfMonth) : 0.0));
        
        // Yearly stats
        stats.setYearlyOrders(orderRepository.countOrdersSince(startOfYear));
        stats.setYearlyRevenue(BigDecimal.valueOf(orderRepository.getTotalRevenueSince(startOfYear) != null ? 
            orderRepository.getTotalRevenueSince(startOfYear) : 0.0));
        
        // Total stats
        stats.setTotalProducts(productRepository.count());
        stats.setTotalUsers(userRepository.count());
        stats.setTotalOrders(orderRepository.count());
        
        return stats;
    }

    public SalesReportResponse getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByDateRange(startDate, endDate);
        
        SalesReportResponse report = new SalesReportResponse();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalOrders((long) orders.size());
        
        BigDecimal totalRevenue = orders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        report.setTotalRevenue(totalRevenue);
        report.setAverageOrderValue(orders.isEmpty() ? BigDecimal.ZERO : 
            totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, BigDecimal.ROUND_HALF_UP));
        
        // Sales by day
        Map<String, BigDecimal> salesByDay = orders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
                .collect(Collectors.groupingBy(
                    order -> order.getCreatedAt().toLocalDate().toString(),
                    Collectors.mapping(Order::getTotalAmount, 
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        report.setSalesByDay(salesByDay);
        
        // Sales by order type
        Map<String, BigDecimal> salesByType = orders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
                .collect(Collectors.groupingBy(
                    order -> order.getOrderType().toString(),
                    Collectors.mapping(Order::getTotalAmount, 
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        report.setSalesByOrderType(salesByType);
        
        return report;
    }

    public ProductReportResponse getProductReport() {
        ProductReportResponse report = new ProductReportResponse();
        
        // This would require more complex queries to get best selling products
        // For now, returning basic stats
        report.setTotalProducts(productRepository.count());
        report.setActiveProducts(productRepository.findByIsActiveTrueOrderByCreatedAtDesc(
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        
        return report;
    }

    public List<TopProductResponse> getTopSellingProducts(int limit) {
        // This would require a complex query joining orders, order_items, and products
        // For now, returning empty list - would need to implement proper query
        return List.of();
    }

    public CustomerReportResponse getCustomerReport() {
        CustomerReportResponse report = new CustomerReportResponse();
        
        long totalCustomers = userRepository.count();
        report.setTotalCustomers(totalCustomers);
        
        // Would need more complex queries for customer analytics
        report.setNewCustomersThisMonth(0L);
        report.setActiveCustomers(0L);
        
        return report;
    }
}
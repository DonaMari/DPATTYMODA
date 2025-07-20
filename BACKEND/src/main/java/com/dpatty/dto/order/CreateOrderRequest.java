package com.dpatty.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "Dirección de envío es requerida")
    private String shippingAddress;

    @NotBlank(message = "Ciudad es requerida")
    private String shippingCity;

    private String shippingState;
    private String shippingZipCode;
    private String notes;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal discountAmount;
    private String couponCode;
}
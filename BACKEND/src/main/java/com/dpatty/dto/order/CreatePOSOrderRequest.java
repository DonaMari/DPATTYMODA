package com.dpatty.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreatePOSOrderRequest {
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerDocument;
    
    @NotEmpty(message = "Items son requeridos")
    private List<POSOrderItem> items;
    
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private String notes;

    @Data
    public static class POSOrderItem {
        @NotNull(message = "ID de variante de producto es requerido")
        private Long productVariantId;

        @NotNull(message = "Cantidad es requerida")
        private Integer quantity;

        @NotNull(message = "Precio unitario es requerido")
        private BigDecimal unitPrice;
    }
}
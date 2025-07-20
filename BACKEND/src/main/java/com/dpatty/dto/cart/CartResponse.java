package com.dpatty.dto.cart;

import com.dpatty.dto.product.ProductVariantResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal total;
    private Integer itemCount;

    @Data
    public static class CartItemResponse {
        private Long id;
        private ProductVariantResponse productVariant;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
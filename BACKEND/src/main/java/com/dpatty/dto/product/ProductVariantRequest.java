package com.dpatty.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantRequest {
    private String sku;
    private String size;
    private String color;
    private String colorHex;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
}
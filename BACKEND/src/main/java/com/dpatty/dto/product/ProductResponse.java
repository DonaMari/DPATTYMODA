package com.dpatty.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal comparePrice;
    private String sku;
    private String barcode;
    private Boolean isActive;
    private Boolean isFeatured;
    private BigDecimal weight;
    private String metaTitle;
    private String metaDescription;
    private List<ProductVariantResponse> variants;
    private List<ProductImageResponse> images;
    private List<CategoryResponse> categories;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
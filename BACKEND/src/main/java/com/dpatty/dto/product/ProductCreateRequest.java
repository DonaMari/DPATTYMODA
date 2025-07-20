package com.dpatty.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Nombre del producto es requerido")
    private String name;

    private String description;
    private String shortDescription;

    @NotNull(message = "Precio es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    private BigDecimal comparePrice;

    @NotBlank(message = "SKU es requerido")
    private String sku;

    private String barcode;
    private Boolean isFeatured = false;
    private BigDecimal weight;
    private String metaTitle;
    private String metaDescription;

    private Set<Long> categoryIds;
    private List<ProductVariantRequest> variants;
    private List<String> imageUrls;
    private Integer initialStock;
}
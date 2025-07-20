package com.dpatty.dto.product;

import lombok.Data;

import java.util.List;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean isActive;
    private Integer sortOrder;
    private List<CategoryResponse> children;
}
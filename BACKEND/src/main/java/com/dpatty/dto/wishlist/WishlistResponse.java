package com.dpatty.dto.wishlist;

import com.dpatty.dto.product.ProductResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WishlistResponse {
    private Long id;
    private ProductResponse product;
    private LocalDateTime createdAt;
}
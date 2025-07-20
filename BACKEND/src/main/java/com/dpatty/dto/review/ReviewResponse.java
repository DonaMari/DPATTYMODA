package com.dpatty.dto.review;

import com.dpatty.model.Review;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String userName;
    private Integer rating;
    private String comment;
    private Review.ReviewStatus status;
    private Boolean isVerifiedPurchase;
    private LocalDateTime createdAt;
}
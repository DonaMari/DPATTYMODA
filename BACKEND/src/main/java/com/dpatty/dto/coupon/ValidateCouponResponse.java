package com.dpatty.dto.coupon;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ValidateCouponResponse {
    private String couponCode;
    private Boolean valid;
    private String message;
    private String couponName;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
}
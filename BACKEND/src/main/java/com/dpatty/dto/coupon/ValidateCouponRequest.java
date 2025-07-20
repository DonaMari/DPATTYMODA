package com.dpatty.dto.coupon;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ValidateCouponRequest {
    @NotBlank(message = "Código de cupón es requerido")
    private String couponCode;

    @NotNull(message = "Monto de la orden es requerido")
    @DecimalMin(value = "0.01", message = "El monto de la orden debe ser mayor a 0")
    private BigDecimal orderAmount;
}
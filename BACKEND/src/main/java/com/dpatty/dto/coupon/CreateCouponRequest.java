package com.dpatty.dto.coupon;

import com.dpatty.model.Coupon;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCouponRequest {
    @NotBlank(message = "Código de cupón es requerido")
    private String code;

    @NotBlank(message = "Nombre del cupón es requerido")
    private String name;

    private String description;

    @NotNull(message = "Tipo de descuento es requerido")
    private Coupon.DiscountType discountType;

    @NotNull(message = "Valor de descuento es requerido")
    @DecimalMin(value = "0.01", message = "El valor de descuento debe ser mayor a 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0", message = "El monto mínimo debe ser mayor o igual a 0")
    private BigDecimal minimumAmount;

    @DecimalMin(value = "0.0", message = "El descuento máximo debe ser mayor o igual a 0")
    private BigDecimal maximumDiscount;

    private Integer usageLimit;

    @NotNull(message = "Fecha de inicio es requerida")
    private LocalDateTime validFrom;

    @NotNull(message = "Fecha de fin es requerida")
    private LocalDateTime validUntil;
}
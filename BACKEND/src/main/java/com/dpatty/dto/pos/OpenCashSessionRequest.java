package com.dpatty.dto.pos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenCashSessionRequest {
    @NotNull(message = "Monto de apertura es requerido")
    @DecimalMin(value = "0.0", message = "El monto de apertura debe ser mayor o igual a 0")
    private BigDecimal openingAmount;

    private Long storeId;
    private String notes;
}
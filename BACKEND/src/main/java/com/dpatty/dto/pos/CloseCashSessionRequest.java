package com.dpatty.dto.pos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CloseCashSessionRequest {
    @NotNull(message = "Monto de cierre es requerido")
    @DecimalMin(value = "0.0", message = "El monto de cierre debe ser mayor o igual a 0")
    private BigDecimal closingAmount;

    @NotNull(message = "Monto esperado es requerido")
    @DecimalMin(value = "0.0", message = "El monto esperado debe ser mayor o igual a 0")
    private BigDecimal expectedAmount;

    @NotNull(message = "Ventas en efectivo es requerido")
    @DecimalMin(value = "0.0", message = "Las ventas en efectivo deben ser mayor o igual a 0")
    private BigDecimal cashSalesAmount;

    @NotNull(message = "Ventas con tarjeta es requerido")
    @DecimalMin(value = "0.0", message = "Las ventas con tarjeta deben ser mayor o igual a 0")
    private BigDecimal cardSalesAmount;

    @NotNull(message = "Ventas digitales es requerido")
    @DecimalMin(value = "0.0", message = "Las ventas digitales deben ser mayor o igual a 0")
    private BigDecimal digitalSalesAmount;

    @DecimalMin(value = "0.0", message = "Los gastos deben ser mayor o igual a 0")
    private BigDecimal expensesAmount = BigDecimal.ZERO;

    private String closingNotes;
}
package com.app.mspay.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoRequest {
    private Long carritoId;
    private Long usuarioId;
    private BigDecimal monto;
    private String metodoPago;
}
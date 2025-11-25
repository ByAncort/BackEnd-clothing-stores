package com.app.mspay.dto;

import com.app.mspay.domain.entity.Pago;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class PagoResponse {
    private Long id;
    private Long carritoId;
    private BigDecimal monto;
    private Pago.EstadoPago estado;
    private String referenciaTransaccion;
    private LocalDateTime fechaCreacion;
}
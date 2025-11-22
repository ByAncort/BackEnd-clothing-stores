package com.necronet.mscard.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarritoResponse {
    private Long id;
    private Long usuarioId;
    private List<ItemCarritoResponse> items;
    private BigDecimal subtotal;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
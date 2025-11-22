package com.necronet.mscard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCarritoResponse {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String codigoSku;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String talla;
    private String color;
    private String imagen;
}
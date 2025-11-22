package com.necronet.mscard.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarritoRequest {
    private Long productoId;
    private Integer cantidad;
    private String talla;
    private String color;
}

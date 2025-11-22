package com.necronet.mscard.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "items_carrito")
@Data
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @Column(nullable = false)
    private Long productoId;

    private String nombreProducto;
    private String codigoSku;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario = BigDecimal.ZERO; // CORREGIDO: Inicializar

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO; // CORREGIDO: Inicializar

    private String talla;
    private String color;
    private String imagen;

    @PrePersist
    @PreUpdate
    protected void calcularSubtotal() {
        // CORREGIDO: Manejar posibles nulls
        BigDecimal precio = this.precioUnitario != null ? this.precioUnitario : BigDecimal.ZERO;
        Integer cant = this.cantidad != null ? this.cantidad : 0;
        this.subtotal = precio.multiply(BigDecimal.valueOf(cant));
    }
}
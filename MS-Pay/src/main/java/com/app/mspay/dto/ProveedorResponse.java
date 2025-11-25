package com.app.mspay.dto;

import lombok.Data;

@Data
public class ProveedorResponse {
    private Long id;
    private String nombre;
    private String rut;
    private String direccion;
    private String telefono;
    private String email;
    private Boolean activo;
}
package com.app.mspay.dto;

import lombok.Data;

@Data
public class ProveedorRequest {
    private String rut;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Boolean activo;
}
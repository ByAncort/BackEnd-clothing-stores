package com.app.mspay.web.controller;

import com.app.mspay.domain.entity.Pago;
import com.app.mspay.domain.service.PagoService;
import com.app.mspay.dto.PagoRequest;
import com.app.mspay.dto.PagoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ms-pay/pagos")
@RequiredArgsConstructor
@Tag(name = "Gestión de Pagos", description = "Endpoints para procesar pagos")
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/procesar")
    @Operation(summary = "Procesar un nuevo pago", description = "Recibe datos del carrito y procesa el pago")
    public ResponseEntity<PagoResponse> procesarPago(@Valid @RequestBody PagoRequest request) {
        PagoResponse response = pagoService.procesarPago(request);

        if (response.getEstado() == Pago.EstadoPago.RECHAZADO) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID")
    public ResponseEntity<PagoResponse> obtenerPago(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorId(id));
    }
}
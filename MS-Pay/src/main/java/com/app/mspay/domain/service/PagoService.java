package com.app.mspay.domain.service;

import com.app.mspay.domain.entity.Pago;
import com.app.mspay.domain.repository.PagoRepository;
import com.app.mspay.dto.PagoRequest;
import com.app.mspay.dto.PagoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    // private final CarritoClientService carritoClient; // Si necesitaras comunicarte con carrito

    @Transactional
    public PagoResponse procesarPago(PagoRequest request) {
        log.info("Iniciando procesamiento de pago para carrito ID: {}", request.getCarritoId());

        // 1. Aquí podrías llamar al MS-CARRITO para validar que el monto sea correcto
        // y que el carrito exista y esté activo.

        // 2. Crear la entidad de pago inicial (PENDIENTE)
        Pago pago = Pago.builder()
                .carritoId(request.getCarritoId())
                .usuarioId(request.getUsuarioId())
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .estado(Pago.EstadoPago.PENDIENTE)
                .build();

        pago = pagoRepository.save(pago);

        // 3. Simular integración con pasarela de pagos (Webpay, Stripe, etc.)
        boolean pagoExitoso = simularPasarelaExterna(request);

        if (pagoExitoso) {
            pago.setEstado(Pago.EstadoPago.APROBADO);
            pago.setReferenciaTransaccion(UUID.randomUUID().toString());
            // Aquí llamarías a carrito para "Cerrar" la orden
        } else {
            pago.setEstado(Pago.EstadoPago.RECHAZADO);
        }

        Pago pagoGuardado = pagoRepository.save(pago);
        return mapToResponse(pagoGuardado);
    }

    public PagoResponse obtenerPagoPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        return mapToResponse(pago);
    }

    private boolean simularPasarelaExterna(PagoRequest request) {
        // Lógica simulada: si el monto es positivo, pasa.
        return request.getMonto().doubleValue() > 0;
    }

    private PagoResponse mapToResponse(Pago entity) {
        PagoResponse response = new PagoResponse();
        response.setId(entity.getId());
        response.setCarritoId(entity.getCarritoId());
        response.setMonto(entity.getMonto());
        response.setEstado(entity.getEstado());
        response.setReferenciaTransaccion(entity.getReferenciaTransaccion());
        response.setFechaCreacion(entity.getFechaCreacion());
        return response;
    }
}
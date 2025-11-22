package com.necronet.mscard.interfaces.controller;

import com.necronet.mscard.domain.service.CarritoService;
import com.necronet.mscard.dto.CarritoRequest;
import com.necronet.mscard.dto.CarritoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoResponse> obtenerCarrito(@RequestHeader("X-User-Id") Long usuarioId) {
        CarritoResponse carrito = carritoService.obtenerCarritoPorUsuario(usuarioId);
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/agregar")
    public ResponseEntity<CarritoResponse> agregarProducto(
            @RequestHeader("X-User-Id") Long usuarioId,
            @RequestBody CarritoRequest request) {
        CarritoResponse carrito = carritoService.agregarProducto(usuarioId, request);
        return ResponseEntity.ok(carrito);
    }

    @PutMapping("/actualizar/{itemId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @RequestHeader("X-User-Id") Long usuarioId,
            @PathVariable Long itemId,
            @RequestParam Integer cantidad) {
        CarritoResponse carrito = carritoService.actualizarCantidad(usuarioId, itemId, cantidad);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/remover/{itemId}")
    public ResponseEntity<CarritoResponse> removerProducto(
            @RequestHeader("X-User-Id") Long usuarioId,
            @PathVariable Long itemId) {
        CarritoResponse carrito = carritoService.removerProducto(usuarioId, itemId);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/limpiar")
    public ResponseEntity<Void> limpiarCarrito(@RequestHeader("X-User-Id") Long usuarioId) {
        carritoService.limpiarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
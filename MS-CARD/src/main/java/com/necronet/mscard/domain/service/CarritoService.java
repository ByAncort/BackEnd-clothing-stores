package com.necronet.mscard.domain.service;

import com.necronet.mscard.domain.entity.Carrito;
import com.necronet.mscard.domain.entity.ItemCarrito;
import com.necronet.mscard.domain.repository.CarritoRepository;
import com.necronet.mscard.dto.CarritoRequest;
import com.necronet.mscard.dto.CarritoResponse;
import com.necronet.mscard.dto.ItemCarritoResponse;
import com.necronet.mscard.dto.ProductoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoClientService productoClientService;

    @Transactional
    public CarritoResponse obtenerCarritoPorUsuario(Long usuarioId) {
        log.info("Obteniendo carrito para usuario ID: {}", usuarioId);

        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuarioId)
                .orElseGet(() -> crearCarrito(usuarioId));

        return mapToCarritoResponse(carrito);
    }

    @Transactional
    public CarritoResponse agregarProducto(Long usuarioId, CarritoRequest request) {
        log.info("Agregando producto al carrito. Usuario ID: {}, Producto ID: {}", usuarioId, request.getProductoId());

        // Validar producto
        ProductoResponse producto = productoClientService.consultarProducto(request.getProductoId());
        validarProducto(producto, request);

        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuarioId)
                .orElseGet(() -> crearCarrito(usuarioId));

        // Verificar si el producto ya está en el carrito
        ItemCarrito itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(request.getProductoId()) &&
                        equalsSafe(item.getTalla(), request.getTalla()) &&
                        equalsSafe(item.getColor(), request.getColor()))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            // Actualizar cantidad si ya existe
            itemExistente.setCantidad(itemExistente.getCantidad() + request.getCantidad());
        } else {
            // Crear nuevo item
            ItemCarrito nuevoItem = crearItemCarrito(request, producto);
            carrito.agregarItem(nuevoItem);
        }

        carrito.calcularTotales();
        Carrito carritoGuardado = carritoRepository.save(carrito);

        log.info("Producto agregado al carrito exitosamente");
        return mapToCarritoResponse(carritoGuardado);
    }

    @Transactional
    public CarritoResponse actualizarCantidad(Long usuarioId, Long itemId, Integer nuevaCantidad) {
        log.info("Actualizando cantidad del item. Usuario ID: {}, Item ID: {}, Nueva cantidad: {}",
                usuarioId, itemId, nuevaCantidad);

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        // Validar stock
        ProductoResponse producto = productoClientService.consultarProducto(item.getProductoId());
        if (nuevaCantidad > producto.getStock()) {
            throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock());
        }

        item.setCantidad(nuevaCantidad);
        carrito.calcularTotales();
        Carrito carritoActualizado = carritoRepository.save(carrito);

        return mapToCarritoResponse(carritoActualizado);
    }

    @Transactional
    public CarritoResponse removerProducto(Long usuarioId, Long itemId) {
        log.info("Removiendo producto del carrito. Usuario ID: {}, Item ID: {}", usuarioId, itemId);

        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        carrito.removerItem(item);
        Carrito carritoActualizado = carritoRepository.save(carrito);

        return mapToCarritoResponse(carritoActualizado);
    }

    @Transactional
    public void limpiarCarrito(Long usuarioId) {
        log.info("Limpiando carrito del usuario ID: {}", usuarioId);

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        carrito.limpiarCarrito();
        carritoRepository.save(carrito);
    }

    private Carrito crearCarrito(Long usuarioId) {
        Carrito carrito = new Carrito();
        carrito.setUsuarioId(usuarioId);
        return carritoRepository.save(carrito);
    }

    private ItemCarrito crearItemCarrito(CarritoRequest request, ProductoResponse producto) {
        ItemCarrito item = new ItemCarrito();
        item.setProductoId(request.getProductoId());
        item.setNombreProducto(producto.getNombre());
        item.setCodigoSku(producto.getCodigoSku());
        item.setCantidad(request.getCantidad());
        item.setPrecioUnitario(producto.getPrecio());
        item.setTalla(request.getTalla());
        item.setColor(request.getColor());
        item.setImagen(producto.getImagePrimary());
        return item;
    }

    private void validarProducto(ProductoResponse producto, CarritoRequest request) {
        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock());
        }

    }

    private CarritoResponse mapToCarritoResponse(Carrito carrito) {
        CarritoResponse response = new CarritoResponse();
        response.setId(carrito.getId());
        response.setUsuarioId(carrito.getUsuarioId());
        response.setSubtotal(carrito.getSubtotal());
        response.setTotal(carrito.getTotal());
        response.setFechaCreacion(carrito.getFechaCreacion());
        response.setFechaActualizacion(carrito.getFechaActualizacion());

        List<ItemCarritoResponse> items = carrito.getItems().stream()
                .map(this::mapToItemCarritoResponse)
                .collect(Collectors.toList());
        response.setItems(items);

        return response;
    }

    private ItemCarritoResponse mapToItemCarritoResponse(ItemCarrito item) {
        ItemCarritoResponse response = new ItemCarritoResponse();
        response.setId(item.getId());
        response.setProductoId(item.getProductoId());
        response.setNombreProducto(item.getNombreProducto());
        response.setCodigoSku(item.getCodigoSku());
        response.setCantidad(item.getCantidad());
        response.setPrecioUnitario(item.getPrecioUnitario());
        response.setSubtotal(item.getSubtotal());
        response.setTalla(item.getTalla());
        response.setColor(item.getColor());
        response.setImagen(item.getImagen());
        return response;
    }

    private boolean equalsSafe(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}
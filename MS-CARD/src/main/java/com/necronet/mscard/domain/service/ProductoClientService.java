package com.necronet.mscard.domain.service;

import com.necronet.mscard.dto.ProductoResponse;
import com.necronet.mscard.shared.client.MicroserviceClient;
import com.necronet.mscard.shared.security.TokenContext;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoClientService {

    @Value("${auth.url.productos.get-id}")
    private String PROD_URL_SERVICE;

    private final MicroserviceClient microserviceClient;

    @CircuitBreaker(name = "productosService", fallbackMethod = "fallbackProducto")
    public ProductoResponse consultarProducto(Long productoId) {
        log.info("Consultando producto con ID: {}", productoId);

        String token = TokenContext.getToken();
        String url = PROD_URL_SERVICE + productoId;

        ResponseEntity<ProductoResponse> response = microserviceClient.enviarConToken(
                url,
                HttpMethod.GET,
                null,
                ProductoResponse.class,
                token
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error al obtener producto. Código de estado: {}", response.getStatusCode());
            throw new RuntimeException("Error al obtener producto con ID: " + productoId);
        }

        log.info("Producto obtenido exitosamente: {}", response.getBody());
        return response.getBody();
    }

    // CORREGIDO: Método debe coincidir con el nombre en @CircuitBreaker
    public ProductoResponse fallbackProducto(Long productoId, Exception exception) {
        log.warn("Ejecutando fallback para producto ID: {}. Error: {}", productoId, exception.getMessage());
        return createDefaultProducto(productoId);
    }

    private ProductoResponse createDefaultProducto(Long productoId) {
        ProductoResponse defaultProduct = new ProductoResponse();
        defaultProduct.setId(productoId);
        defaultProduct.setNombre("Producto no disponible");
        defaultProduct.setCodigoSku("ND-" + productoId);
        defaultProduct.setDescripcion("Producto temporalmente no disponible");
        defaultProduct.setPrecio(BigDecimal.ZERO);
        defaultProduct.setCosto(BigDecimal.ZERO);
        defaultProduct.setStock(0);
        defaultProduct.setTallas(Collections.emptyList());
        defaultProduct.setColores(Collections.emptyList());
        defaultProduct.setEspecificaciones(new HashMap<>());

        return defaultProduct;
    }
}
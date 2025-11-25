package com.app.mspay.domain.service;

import com.app.mspay.dto.ProveedorRequest;
import com.app.mspay.dto.ProveedorResponse;
import com.app.mspay.shared.client.MicroserviceClient;
import com.app.mspay.shared.security.TokenContext;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProveedorClientService {

    @Value("${inventory.url.base}")
    private String PROVEEDOR_BASE_URL;

    private final MicroserviceClient microserviceClient;

    // ... (Tus otros métodos get/post siguen igual) ...

    // --- CORRECCIÓN AQUÍ ---
    @CircuitBreaker(name = "proveedorService", fallbackMethod = "fallbackWriteProveedor")
    public ProveedorResponse cambiarEstadoProveedor(Long id, boolean activo) {
        log.info("Cambiando estado proveedor ID: {} a {}", id, activo);

        // Usamos el builder de forma segura
        String url = UriComponentsBuilder.fromUriString(PROVEEDOR_BASE_URL)
                .pathSegment("toggle-activo", "{id}")
                .queryParam("activo", activo)
                .buildAndExpand(id)
                .toUriString();


        return ejecutarSolicitud(url, HttpMethod.PATCH, null, ProveedorResponse.class);
    }

    // ... (Resto del código, métodos genéricos y fallbacks siguen igual) ...

    // Método auxiliar necesario para el código anterior
    private <T> T ejecutarSolicitud(String url, HttpMethod method, Object body, Class<T> responseType) {
        String token = TokenContext.getToken();
        ResponseEntity<T> response = microserviceClient.enviarConToken(url, method, body, responseType, token);
        // Validaciones...
        return response.getBody();
    }

    // Fallbacks...
    public ProveedorResponse fallbackWriteProveedor(Long id, boolean activo, Exception exception) {
        // Nota: El fallback debe coincidir en argumentos con el método original + la excepción
        log.error("Fallback toggle activo. Error: {}", exception.getMessage());
        throw new RuntimeException("Servicio no disponible");
    }
}
package com.app.producto.config.web;

import com.app.producto.shared.client.AuthClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority; // Necesario
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Necesario
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List; // Necesario

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthClientService authClientService;

    public JwtAuthFilter(AuthClientService authClientService) {
        this.authClientService = authClientService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            // 1. Lógica de validación con el microservicio externo (9001/9010)
            if (authClientService.validateToken(token)) {

                // 2. CREACIÓN DE ROLES NECESARIOS (SOLUCIÓN DEL 403)
                // Asignamos ROLE_USER y ROLE_ADMIN para tener permisos de escritura
                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                );

                UsernamePasswordAuthenticationToken authentication =
                        // Asignamos los permisos que acabamos de crear:
                        new UsernamePasswordAuthenticationToken("usuario", null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                // Token inválido (401)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
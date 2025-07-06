package com.marketplace.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.userservice.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Manejador personalizado para errores de autenticación.
 * Se ejecuta cuando un usuario no autenticado intenta acceder a un recurso protegido.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Authentication failed for request: {} {} - Reason: {}",
                request.getMethod(), request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
            "AUTHENTICATION_REQUIRED",
            "Credenciales inválidas o token expirado. Por favor, inicia sesión nuevamente."
        );

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}

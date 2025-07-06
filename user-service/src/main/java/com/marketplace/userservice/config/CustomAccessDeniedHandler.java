package com.marketplace.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.userservice.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.warn("Access denied for request: {} {} - Reason: {}",
                request.getMethod(), request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
            "ACCESS_DENIED",
            "No tienes permisos suficientes para acceder a este recurso. Verifica tus credenciales y roles asignados."
        );

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}

package marketplace.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import marketplace.orderservice.dto.AuthErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Manejador personalizado para errores de acceso denegado (403 Forbidden).
 * Se ejecuta cuando un usuario autenticado intenta acceder a un recurso
 * para el cual no tiene los permisos necesarios.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        AuthErrorResponseDto errorResponse = AuthErrorResponseDto.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("ACCESS_DENIED")
                .errorCode("AUTH_403")
                .message("Acceso denegado: No tienes permisos para acceder a este recurso")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .suggestion("Contacta al administrador del sistema para obtener los permisos necesarios")
                .details("El usuario está autenticado pero no tiene los roles/permisos requeridos")
                .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}

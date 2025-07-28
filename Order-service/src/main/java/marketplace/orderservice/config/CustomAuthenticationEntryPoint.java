package marketplace.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import marketplace.orderservice.dto.AuthErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Punto de entrada personalizado para errores de autenticación (401 Unauthorized).
 * Se ejecuta cuando un usuario no autenticado intenta acceder a un recurso protegido
 * o cuando las credenciales proporcionadas son inválidas.
 *
 * Proporciona respuestas específicas según el tipo de error JWT.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        AuthErrorResponseDto errorResponse = buildErrorResponse(request, authException);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private AuthErrorResponseDto buildErrorResponse(HttpServletRequest request, AuthenticationException authException) {
        String authHeader = request.getHeader("Authorization");

        // Determinar el tipo específico de error
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return AuthErrorResponseDto.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error("MISSING_TOKEN")
                    .errorCode("AUTH_001")
                    .message("Token de autenticación requerido")
                    .path(request.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .suggestion("Incluye un token JWT válido en el header Authorization con el formato 'Bearer <token>'")
                    .build();
        }

        // Analizar el tipo de excepción JWT
        if (authException instanceof InvalidBearerTokenException) {
            InvalidBearerTokenException jwtException = (InvalidBearerTokenException) authException;
            String message = jwtException.getMessage();

            if (message != null) {
                if (message.contains("expired") || message.contains("Jwt expired")) {
                    return AuthErrorResponseDto.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .error("TOKEN_EXPIRED")
                            .errorCode("AUTH_002")
                            .message("El token JWT ha expirado")
                            .path(request.getRequestURI())
                            .timestamp(LocalDateTime.now())
                            .suggestion("Obtén un nuevo token de acceso utilizando tu refresh token o vuelve a autenticarte")
                            .details("El token proporcionado es válido pero ha superado su tiempo de vida")
                            .build();
                }

                if (message.contains("malformed") || message.contains("Invalid JWT") || message.contains("Unable to parse")) {
                    return AuthErrorResponseDto.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .error("TOKEN_MALFORMED")
                            .errorCode("AUTH_003")
                            .message("El token JWT está malformado o es inválido")
                            .path(request.getRequestURI())
                            .timestamp(LocalDateTime.now())
                            .suggestion("Verifica que el token esté correctamente codificado y no haya sido modificado")
                            .details("El token no tiene un formato JWT válido")
                            .build();
                }

                if (message.contains("signature") || message.contains("Signed JWT")) {
                    return AuthErrorResponseDto.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .error("TOKEN_SIGNATURE_INVALID")
                            .errorCode("AUTH_004")
                            .message("La firma del token JWT no es válida")
                            .path(request.getRequestURI())
                            .timestamp(LocalDateTime.now())
                            .suggestion("El token puede haber sido modificado o no fue emitido por el servidor de autenticación correcto")
                            .details("La verificación de la firma del token falló")
                            .build();
                }
            }
        }

        // Error genérico de autenticación
        return AuthErrorResponseDto.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("AUTHENTICATION_FAILED")
                .errorCode("AUTH_000")
                .message("Error de autenticación: " + authException.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .suggestion("Verifica que tu token sea válido y no haya expirado")
                .details(authException.getClass().getSimpleName())
                .build();
    }
}

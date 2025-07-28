package marketplace.orderservice.exception;

import lombok.extern.slf4j.Slf4j;
import marketplace.orderservice.dto.ApiResponseDTO;
import marketplace.orderservice.dto.AuthErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleProductNotFound(ProductNotFoundException ex) {
        log.error("Product not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleInsufficientStock(InsufficientStockException ex) {
        log.error("Insufficient stock: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<AuthErrorResponseDto> handleJwtExpired(JwtExpiredException ex) {
        log.error("JWT token expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthErrorResponseDto.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("TOKEN_EXPIRED")
                        .errorCode("AUTH_002")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .suggestion("Obtén un nuevo token de acceso utilizando tu refresh token o vuelve a autenticarte")
                        .details(ex.getExpiredAt() != null ? "Token expiró en: " + ex.getExpiredAt() : "Token ha expirado")
                        .build());
    }

    @ExceptionHandler(JwtMalformedException.class)
    public ResponseEntity<AuthErrorResponseDto> handleJwtMalformed(JwtMalformedException ex) {
        log.error("JWT token malformed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthErrorResponseDto.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("TOKEN_MALFORMED")
                        .errorCode("AUTH_003")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .suggestion("Verifica que el token esté correctamente codificado y no haya sido modificado")
                        .details("Tipo de token: " + ex.getTokenType())
                        .build());
    }

    @ExceptionHandler(JwtMissingException.class)
    public ResponseEntity<AuthErrorResponseDto> handleJwtMissing(JwtMissingException ex) {
        log.error("JWT token missing: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthErrorResponseDto.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("MISSING_TOKEN")
                        .errorCode("AUTH_001")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .suggestion("Incluye un token JWT válido en el header Authorization con el formato 'Bearer <token>'")
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AuthErrorResponseDto> handleAuthentication(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthErrorResponseDto.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("AUTHENTICATION_FAILED")
                        .errorCode("AUTH_000")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .suggestion("Verifica que tu token sea válido y no haya expirado")
                        .details(ex.getClass().getSimpleName())
                        .build());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidation(ValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation errors: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message("Validation failed: " + errors.toString())
                        .build());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("External service error - Status: {}, Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());

        String message = switch (ex.getStatusCode().value()) {
            case 404 -> "Requested resource not found in external service";
            case 401, 403 -> "Authentication failed with external service";
            case 500 -> "External service is experiencing issues";
            case 503 -> "External service is temporarily unavailable";
            default -> "External service error occurred";
        };

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message(message)
                        .build());
    }

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleWebClientException(WebClientException ex) {
        log.error("External service communication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message("Service temporarily unavailable. Please try again later.")
                        .build());
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleTimeout(TimeoutException ex) {
        log.error("Request timeout: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message("Request timeout. Please try again.")
                        .build());
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleOrderException(OrderException ex) {
        log.error("Order processing error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message("Invalid request: " + ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.builder()
                        .success(false)
                        .message("An unexpected error occurred. Please contact support.")
                        .build());
    }
}

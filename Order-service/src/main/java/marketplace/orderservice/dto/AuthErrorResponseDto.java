package marketplace.orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO específico para errores de autenticación con información detallada.
 */
@Data
@Builder
public class AuthErrorResponseDto {
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private LocalDateTime timestamp;
    private String suggestion;
    private Object details;
}

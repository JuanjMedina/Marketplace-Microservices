package com.marketplace.userservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("User not found", ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("User already exists", ex.getMessage()));
    }

    @ExceptionHandler(KeycloakOperationException.class)
    public ResponseEntity<ErrorResponse> handleKeycloakOperation(KeycloakOperationException ex) {
        log.error("Keycloak operation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Keycloak operation error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal error", "An unexpected error occurred. Please try again later."));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.warn("Authorization denied: User doesn't have sufficient permissions for this resource");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                    "ACCESS_DENIED",
                    "No tienes permisos suficientes para acceder a este recurso. Verifica tus credenciales y roles asignados."
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                    "ACCESS_DENIED",
                    "Acceso denegado. No tienes autorización para realizar esta operación."
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                    "AUTHENTICATION_FAILED",
                    "Credenciales inválidas o token expirado. Por favor, inicia sesión nuevamente."
                ));
    }
}
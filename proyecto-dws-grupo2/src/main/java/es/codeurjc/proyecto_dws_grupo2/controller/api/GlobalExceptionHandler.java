package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Recurso no encontrado
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Map.of(
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
            )
        );
    }

    // 403 - Acceso denegado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            Map.of(
                "status", 403,
                "error", "Forbidden",
                "message", "No tienes permiso para acceder a este recurso"
            )
        );
    }

    // 413 - Fichero demasiado grande
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
            Map.of(
                "status", 413,
                "error", "Payload Too Large",
                "message", "El fichero supera el tamaño máximo permitido"
            )
        );
    }

    // 404 - Entidad no encontrada (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Map.of(
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
            )
        );
    }

    // 500 - Error genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", ex.getMessage()
            )
        );
    }
}

//EXAMPLE USE: GET {{baseUrl}}/api/v1/reviews/9999
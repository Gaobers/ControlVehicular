package org.esfe.excepciones;

import jakarta.persistence.EntityNotFoundException;
import org.esfe.dtos.RespuestaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(new RespuestaDTO<>(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleEntityNotFound(
            EntityNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RespuestaDTO<>(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleValidation(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación");

        return ResponseEntity.badRequest()
                .body(new RespuestaDTO<>(
                        false,
                        mensaje,
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaDTO<Void>> handleGeneric(
            Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespuestaDTO<>(
                        false,
                        "Error interno del servidor",
                        null
                ));
    }
}
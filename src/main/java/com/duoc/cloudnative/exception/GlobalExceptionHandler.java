package com.duoc.cloudnative.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores lanzados manualmente desde los services.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Solicitud inválida",
                ex.getMessage(),
                request.getRequestURI(),
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Maneja errores de validación generados por @Valid en los DTO.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarErroresDeValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> detalles = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación",
                "Uno o más campos enviados no cumplen con las validaciones requeridas.",
                request.getRequestURI(),
                detalles
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Maneja errores de integridad de datos, por ejemplo correos o nombres únicos repetidos.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflicto con la base de datos",
                "No se pudo guardar la información porque existe un conflicto con los datos enviados.",
                request.getRequestURI(),
                List.of("Revise si está intentando registrar información duplicada o inválida.")
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Maneja errores cuando se envía un dato con tipo incorrecto, por ejemplo un ID con texto.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String mensaje = "El parámetro '" + ex.getName() + "' tiene un formato inválido.";

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Tipo de dato inválido",
                mensaje,
                request.getRequestURI(),
                List.of(mensaje)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

        @ExceptionHandler(AmazonServiceException.class)
        public ResponseEntity<ErrorResponse> manejarAmazonServiceException(
                AmazonServiceException ex,
                HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                "Error de servicio AWS S3",
                "AWS S3 respondió con un error al procesar la solicitud.",
                request.getRequestURI(),
                List.of(ex.getErrorMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
        }

        @ExceptionHandler(AmazonClientException.class)
        public ResponseEntity<ErrorResponse> manejarAmazonClientException(
                AmazonClientException ex,
                HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Error de conexión con AWS S3",
                "No fue posible conectarse con AWS S3. Revise credenciales, región, bucket o estado del laboratorio.",
                request.getRequestURI(),
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
        }

    // Maneja cualquier error no controlado.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExceptionGeneral(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                "Ocurrió un error inesperado en la aplicación.",
                request.getRequestURI(),
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
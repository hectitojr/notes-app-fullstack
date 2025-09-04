package com.zoedatalab.notesbackend.web.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ApiError> handleNoteNotFound(NoteNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.code(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoryNotFound(CategoryNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.code(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(BadRequestDomainException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestDomainException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.code(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var fields = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ApiError.FieldError(f.getField(), f.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED,
                "El request contiene campos inválidos", req, fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        var fields = ex.getConstraintViolations().stream()
                .map(v -> new ApiError.FieldError(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED,
                "El request incumple restricciones de validación", req, fields);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, ErrorCode.DATA_CONFLICT,
                "La operación viola restricciones de integridad de datos", req, List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.MALFORMED_JSON,
                "JSON inválido o no legible", req, List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        var fe = new ApiError.FieldError(ex.getName(),
                "Parámetro con tipo incorrecto. Se esperaba %s".formatted(
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "el tipo correcto"));
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED,
                "Parámetro inválido", req, List.of(fe));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCode.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "Error inesperado. Si persiste, contacte soporte.", req, List.of());
    }

    // Helper
    private ResponseEntity<ApiError> build(HttpStatus status, ErrorCode code, String message,
                                           HttpServletRequest req, List<ApiError.FieldError> fields) {
        return ResponseEntity.status(status)
                .body(ApiError.of(status.value(), code, message, req.getRequestURI(), fields));
    }
}

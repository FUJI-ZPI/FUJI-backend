package com.zpi.fujibackend.common.exception;


import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Hidden
class GlobalExceptionHandler {

    private static final class ErrorMessages {
        private static final String MINIO_ERROR = "An internal file storage error occurred";
        private static final String INTERNAL_ERROR = "An internal error occurred";
        private static final String FORBIDDEN = "Forbidden";
        private static final String METHOD_NOT_ALLOWED = "Method not allowed";
        private static final String MALFORMED_JSON = "Malformed JSON or wrong data types";
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex) {
        log.debug("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse.now(ex.getMessage()));
    }


    @ExceptionHandler(FileStorageException.class)
    ResponseEntity<ApiErrorResponse> handleStorageError(FileStorageException ex) {
        log.error("Storage error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiErrorResponse.now(ErrorMessages.MINIO_ERROR));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.debug("Method argument not valid: {}", ex.getMessage(), ex);
        String details = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse.now(details));

    }


    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiErrorResponse.now(ErrorMessages.FORBIDDEN));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not allowed: {}", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiErrorResponse.now(ErrorMessages.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> handleBadJson(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse.now(ErrorMessages.MALFORMED_JSON));
    }


    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleOther(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiErrorResponse.now(ErrorMessages.INTERNAL_ERROR));
    }


}

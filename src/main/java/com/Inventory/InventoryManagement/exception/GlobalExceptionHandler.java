package com.Inventory.InventoryManagement.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Error occurred : MethodArgumentNotValidException");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        logger.error("Error occurred : ConstraintViolationException");
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv ->
                errors.put(cv.getPropertyPath().toString(), cv.getMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        logger.error("Error occurred : MissingServletRequestParameterException");
        return ResponseEntity.badRequest().body("Missing required parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidTypeExceptions(HttpMessageNotReadableException ex) {
        logger.error("Error occurred : HttpMessageNotReadableException");
        if (ex.getCause() instanceof InvalidFormatException) {
            return ResponseEntity.badRequest().body("Invalid data type. Please check your input values.");
        }
        return ResponseEntity.badRequest().body("Malformed request. Please check the request body.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Error occurred : MethodArgumentTypeMismatchException");
        if (ex.getRequiredType() == LocalDate.class) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use yyyy-MM-dd");
        }
        return ResponseEntity.badRequest().body("Invalid parameter: " + ex.getName());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<?> handleDateParseError(DateTimeParseException ex) {
        logger.error("Error occurred : DateTimeParseException");
        return ResponseEntity.badRequest().body("Invalid date format. Please use yyyy-MM-dd");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException ex) {
        logger.error("Error occurred : EntityNotFoundException");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Error occurred : DataIntegrityViolationException");
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Database error: " + ex.getRootCause().getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        logger.error("Error occurred : BadCredentialsException");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthentication(AuthenticationException ex) {
        logger.error("Error occurred : AuthenticationException");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        logger.error("Error occurred : AccessDeniedException");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to access this resource.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.error("Error occurred : HttpRequestMethodNotSupportedException");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Request method not supported: " + ex.getMethod());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointer(NullPointerException ex) {
        logger.error("Error occurred : NullPointerException");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected null value encountered.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeExceptions(RuntimeException ex) {
        logger.error("Error occurred : RuntimeException");
        return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        logger.error("Error occurred : Exception");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later.");
    }
}
package com.qualimark.ecommerce.productService.exception;

import com.qualimark.ecommerce.productService.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API error handling.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static String extractPath(String description) {
        if (description == null) {
            return "";
        }
        // Typical format: "uri=/api/..."; strip the leading "uri=" when present
        return description.startsWith("uri=") ? description.substring(4) : description;
    }

    private static boolean isSensitivePath(String path) {
        if (path == null) {
            return false;
        }
        return path.startsWith("/api/users")
                || path.startsWith("/api/sessions")
                || path.startsWith("/api/rbac")
                || path.startsWith("/api/profile")
                || path.startsWith("/api/protected");
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Validation error: {}", ex.getMessage());

        List<ErrorResponseDto.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> toValidationError(fieldError))
                .collect(Collectors.toList());

        ErrorResponseDto errorResponse = ErrorResponseDto.validationError(
                        "Request validation failed", validationErrors)
                .withPath(request.getDescription(false))
                .withMethod(request.getHeader("X-Request-Method"));

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        log.warn("Constraint violation: {}", ex.getMessage());

        List<ErrorResponseDto.ValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> toValidationError(violation))
                .collect(Collectors.toList());

        ErrorResponseDto errorResponse = ErrorResponseDto.validationError(
                        "Request validation failed", validationErrors)
                .withPath(request.getDescription(false))
                .withMethod(request.getHeader("X-Request-Method"));

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle Illegal Argument Exception
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        ErrorResponseDto errorResponse = ErrorResponseDto.badParams(ex.getMessage())
                .withPath(request.getDescription(false))
                .withMethod(request.getHeader("X-Request-Method"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.notFound(ex.getMessage())
                .withPath(request.getDescription(false))
                .withMethod(request.getHeader("X-Request-Method"));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }


    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceAlreadyExistException(
            ResourceAlreadyExistException ex, WebRequest request) {

        log.warn("Resource already exist: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.conflict(ex.getMessage())
                .withPath(request.getDescription(false))
                .withMethod(request.getHeader("X-Request-Method"));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.internalServerError(
                        "An unexpected error occurred")
                .withPath(request.getDescription(false))
                .withMethod(request.getHeader("X-Request-Method"));

        // Add stack trace in development environment
        if (isDevelopmentEnvironment()) {
            errorResponse.withStackTrace(getStackTrace(ex));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handle unsupported media type (e.g., XML sent to JSON endpoint)
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex, WebRequest request) {
        log.warn("Unsupported media type: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message("Unsupported content type")
                .errorCode("UNSUPPORTED_MEDIA_TYPE")
                .description(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getDescription(false))
                .method(request.getHeader("X-Request-Method"))
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    /**
     * Handle HTTP method not supported -> 405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("Method not allowed")
                .errorCode("METHOD_NOT_ALLOWED")
                .description(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getDescription(false))
                .method(request.getHeader("X-Request-Method"))
                .build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }


    /**
     * Handle bad request parameter issues -> 400
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequestParams(Exception ex, WebRequest request) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid request")
                .errorCode("BAD_REQUEST")
                .description(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getDescription(false))
                .method(request.getHeader("X-Request-Method"))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle malformed JSON bodies -> 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        String path = extractPath(request.getDescription(false));
        if ("/api/sessions/validate".equals(path)) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("error", "Invalid or expired session");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Malformed JSON request")
                .errorCode("MALFORMED_JSON")
                .description(ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getDescription(false))
                .method(request.getHeader("X-Request-Method"))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }


    /**
     * Map FieldError to ValidationError
     */
    private ErrorResponseDto.ValidationError toValidationError(FieldError fieldError) {
        return ErrorResponseDto.ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .code(fieldError.getCode())
                .build();
    }

    /**
     * Map ConstraintViolation to ValidationError
     */
    private ErrorResponseDto.ValidationError toValidationError(ConstraintViolation<?> violation) {
        return ErrorResponseDto.ValidationError.builder()
                .field(violation.getPropertyPath().toString())
                .message(violation.getMessage())
                .rejectedValue(violation.getInvalidValue())
                .code(violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName())
                .build();
    }

    /**
     * Check if running in development environment
     */
    private boolean isDevelopmentEnvironment() {
        String profile = System.getProperty("spring.profiles.active", "default");
        return "dev".equals(profile) || "development".equals(profile);
    }

    /**
     * Get stack trace as string
     */
    private String getStackTrace(Exception ex) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
} 
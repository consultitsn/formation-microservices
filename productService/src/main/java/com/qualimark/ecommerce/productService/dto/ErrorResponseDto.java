package com.qualimark.ecommerce.productService.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive error response DTO for API error handling.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Error message
     */
    private String message;

    /**
     * Error code for client handling
     */
    private String errorCode;

    /**
     * Detailed error description
     */
    private String description;

    /**
     * Timestamp when error occurred
     */
    private LocalDateTime timestamp;

    /**
     * Request path that caused the error
     */
    private String path;

    /**
     * Request method that caused the error
     */
    private String method;

    /**
     * Validation errors (for validation failures)
     */
    private List<ValidationError> validationErrors;

    /**
     * Additional error details
     */
    private Map<String, Object> details;

    /**
     * Stack trace (only in development)
     */
    private String stackTrace;

    /**
     * Correlation ID for tracking
     */
    private String correlationId;

    /**
     * Create error response for validation failures
     */
    public static ErrorResponseDto validationError(String message, List<ValidationError> errors) {
        return ErrorResponseDto.builder()
                .status(400)
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .description("Request validation failed")
                .timestamp(LocalDateTime.now())
                .validationErrors(errors)
                .build();
    }

    /**
     * Create error response for validation failures
     */
    public static ErrorResponseDto badParams(String message) {
        return ErrorResponseDto.builder()
                .status(400)
                .message(message)
                .errorCode("PARAM_ERROR")
                .description("Request params validation failed")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response for not found
     */
    public static ErrorResponseDto notFound(String message) {
        return ErrorResponseDto.builder()
                .status(404)
                .message(message)
                .errorCode("NOT_FOUND")
                .description("Requested resource not found")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response for not found
     */
    public static ErrorResponseDto conflict(String message) {
        return ErrorResponseDto.builder()
                .status(409)
                .message(message)
                .errorCode("CONFLICT")
                .description("Ressource already exists!")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response for unauthorized
     */
    public static ErrorResponseDto unauthorized(String message) {
        return ErrorResponseDto.builder()
                .status(401)
                .message(message)
                .errorCode("UNAUTHORIZED")
                .description("Authentication required")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response for forbidden
     */
    public static ErrorResponseDto forbidden(String message) {
        return ErrorResponseDto.builder()
                .status(403)
                .message(message)
                .errorCode("FORBIDDEN")
                .description("Access denied")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response for internal server error
     */
    public static ErrorResponseDto internalServerError(String message) {
        return ErrorResponseDto.builder()
                .status(500)
                .message(message)
                .errorCode("INTERNAL_SERVER_ERROR")
                .description("An internal server error occurred")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response for rate limit exceeded
     */
    public static ErrorResponseDto rateLimitExceeded(String message) {
        return ErrorResponseDto.builder()
                .status(429)
                .message(message)
                .errorCode("RATE_LIMIT_EXCEEDED")
                .description("Rate limit exceeded")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response for service unavailable
     */
    public static ErrorResponseDto serviceUnavailable(String message) {
        return ErrorResponseDto.builder()
                .status(503)
                .message(message)
                .errorCode("SERVICE_UNAVAILABLE")
                .description("Service temporarily unavailable")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Add correlation ID to error response
     */
    public ErrorResponseDto withCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    /**
     * Add path to error response
     */
    public ErrorResponseDto withPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * Add method to error response
     */
    public ErrorResponseDto withMethod(String method) {
        this.method = method;
        return this;
    }

    /**
     * Add details to error response
     */
    public ErrorResponseDto withDetails(Map<String, Object> details) {
        this.details = details;
        return this;
    }

    /**
     * Add stack trace to error response (development only)
     */
    public ErrorResponseDto withStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    /**
     * Inner class for validation errors
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        /**
         * Field name that failed validation
         */
        private String field;

        /**
         * Validation error message
         */
        private String message;

        /**
         * Rejected value
         */
        private Object rejectedValue;

        /**
         * Validation code
         */
        private String code;
    }
} 
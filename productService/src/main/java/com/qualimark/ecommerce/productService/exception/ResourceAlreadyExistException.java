package com.qualimark.ecommerce.productService.exception;

public class ResourceAlreadyExistException extends RuntimeException {
    public ResourceAlreadyExistException(String message) {
        super(message);
    }

    public ResourceAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceAlreadyExistException(String resourceType, String resourceId) {
        super(String.format("%s with name %s already exist", resourceType, resourceId));
    }
}

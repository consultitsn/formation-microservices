package com.example.orderservice.exception;

/**
 * Exception levée lorsqu'un produit n'est pas disponible
 * 
 * Cette exception illustre la gestion des erreurs métier dans
 * une architecture microservices.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class ProductNotAvailableException extends RuntimeException {
    
    public ProductNotAvailableException(String message) {
        super(message);
    }
    
    public ProductNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

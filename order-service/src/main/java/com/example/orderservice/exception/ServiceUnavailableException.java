package com.example.orderservice.exception;

/**
 * Exception levée lorsqu'un service externe n'est pas disponible
 * 
 * Cette exception illustre la gestion des erreurs dans une architecture
 * microservices et la communication inter-services.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class ServiceUnavailableException extends RuntimeException {
    
    public ServiceUnavailableException(String message) {
        super(message);
    }
    
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

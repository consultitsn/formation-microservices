package com.example.orderservice.exception;

/**
 * Exception levée lorsqu'une commande n'est pas trouvée
 * 
 * Cette exception illustre la gestion des erreurs métier dans
 * une architecture microservices.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderNotFoundException extends RuntimeException {
    
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

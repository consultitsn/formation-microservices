package com.example.orderservice.model;

/**
 * Enumération des statuts possibles pour une commande
 * 
 * Cette énumération illustre le cycle de vie d'une commande
 * dans une architecture microservices avec gestion des états.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public enum OrderStatus {
    
    /**
     * Commande créée, en attente de traitement
     */
    PENDING("En attente"),
    
    /**
     * Commande confirmée, en cours de traitement
     */
    CONFIRMED("Confirmée"),
    
    /**
     * Commande en cours de préparation
     */
    PREPARING("En préparation"),
    
    /**
     * Commande prête pour la livraison
     */
    READY_FOR_DELIVERY("Prête pour livraison"),
    
    /**
     * Commande en cours de livraison
     */
    IN_DELIVERY("En livraison"),
    
    /**
     * Commande livrée avec succès
     */
    DELIVERED("Livrée"),
    
    /**
     * Commande annulée
     */
    CANCELLED("Annulée"),
    
    /**
     * Commande en attente d'annulation (pour compensation)
     */
    PENDING_CANCELLATION("En attente d'annulation"),
    
    /**
     * Commande échouée (erreur de traitement)
     */
    FAILED("Échouée");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Vérifie si le statut indique que la commande est active
     * 
     * @return true si la commande est active
     */
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == PREPARING || 
               this == READY_FOR_DELIVERY || this == IN_DELIVERY;
    }
    
    /**
     * Vérifie si le statut indique que la commande est terminée
     * 
     * @return true si la commande est terminée
     */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED || this == FAILED;
    }
    
    /**
     * Vérifie si le statut permet l'annulation
     * 
     * @return true si la commande peut être annulée
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }
    
    /**
     * Vérifie si le statut permet la confirmation
     * 
     * @return true si la commande peut être confirmée
     */
    public boolean canBeConfirmed() {
        return this == PENDING;
    }
    
    /**
     * Vérifie si le statut permet la livraison
     * 
     * @return true si la commande peut être marquée comme livrée
     */
    public boolean canBeDelivered() {
        return this == CONFIRMED || this == IN_DELIVERY;
    }
}

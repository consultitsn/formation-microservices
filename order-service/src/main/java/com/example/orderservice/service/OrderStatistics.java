package com.example.orderservice.service;

/**
 * Classe pour les statistiques des commandes
 * 
 * Cette classe illustre la structure des données pour les
 * statistiques et métriques dans une architecture microservices.
 * 
 * @author Formation Microservices
 * @version 1.0
 */
public class OrderStatistics {
    
    private final long totalOrders;
    private final long pendingOrders;
    private final long confirmedOrders;
    private final long deliveredOrders;
    private final long cancelledOrders;
    
    public OrderStatistics(long totalOrders, long pendingOrders, long confirmedOrders, 
                          long deliveredOrders, long cancelledOrders) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.confirmedOrders = confirmedOrders;
        this.deliveredOrders = deliveredOrders;
        this.cancelledOrders = cancelledOrders;
    }
    
    public long getTotalOrders() {
        return totalOrders;
    }
    
    public long getPendingOrders() {
        return pendingOrders;
    }
    
    public long getConfirmedOrders() {
        return confirmedOrders;
    }
    
    public long getDeliveredOrders() {
        return deliveredOrders;
    }
    
    public long getCancelledOrders() {
        return cancelledOrders;
    }
    
    public double getCompletionRate() {
        if (totalOrders == 0) {
            return 0.0;
        }
        return (double) deliveredOrders / totalOrders * 100;
    }
    
    public double getCancellationRate() {
        if (totalOrders == 0) {
            return 0.0;
        }
        return (double) cancelledOrders / totalOrders * 100;
    }
    
    @Override
    public String toString() {
        return "OrderStatistics{" +
                "totalOrders=" + totalOrders +
                ", pendingOrders=" + pendingOrders +
                ", confirmedOrders=" + confirmedOrders +
                ", deliveredOrders=" + deliveredOrders +
                ", cancelledOrders=" + cancelledOrders +
                ", completionRate=" + String.format("%.2f", getCompletionRate()) + "%" +
                ", cancellationRate=" + String.format("%.2f", getCancellationRate()) + "%" +
                '}';
    }
}

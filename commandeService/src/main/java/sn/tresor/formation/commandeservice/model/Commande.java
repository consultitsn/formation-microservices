package sn.tresor.formation.commandeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "L'ID du client est obligatoire")
    @Column(nullable = false)
    private String customerId; // Id du client

    @Column(nullable = false)
    private String status; // Statut de la commande (PENDING, CONFIRMED, DELIVERED, CANCELLED)

    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.01", message = " Le montant total doit etre superieur a 0")
    @DecimalMax(value = "9999999.99", message = "Le montant ne doit pas etre superieur a 999999.99")
    @Column(nullable = false)
    private BigDecimal totalAmount; // Motant total de la commande

    @Column(nullable = false, name = "created_at")
    private LocalDateTime creationDate; // Date commande

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "commande",  fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    private List<DetailsCommande> detailsCommandes;

    @PrePersist
    public  void postPersist() {
        this.creationDate = LocalDateTime.now();
        this.lastUpdate = LocalDateTime.now();
    }

    @PreUpdate
    public void postUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }

    public Commande() {
    }

    public Commande(String customerId, BigDecimal totalAmount) {
        this.customerId = customerId;
        this.status = "PENDING";
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public List<DetailsCommande> getDetailsCommandes() {
        return detailsCommandes;
    }
}

package sn.tresor.formation.commandeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.tresor.formation.commandeservice.model.Commande;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}

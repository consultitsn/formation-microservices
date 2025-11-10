package sn.tresor.formation.commandeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.tresor.formation.commandeservice.model.DetailsCommande;

public interface DetailsCommandeRepository extends JpaRepository<DetailsCommande, Long> {
}

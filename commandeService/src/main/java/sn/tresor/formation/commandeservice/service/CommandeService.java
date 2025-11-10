package sn.tresor.formation.commandeservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.tresor.formation.commandeservice.model.Commande;
import sn.tresor.formation.commandeservice.repository.CommandeRepository;

import java.util.List;

@Service
@Transactional
public class CommandeService {

    private final CommandeRepository commandeRepository;

    public CommandeService(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    public List<Commande> getAllOrders() {
        return commandeRepository.findAll();
    }
}

package sn.tresor.formation.commandeservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.tresor.formation.commandeservice.model.Commande;
import sn.tresor.formation.commandeservice.service.CommandeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping
    public List<Commande> getAllOrders() {
        return commandeService.getAllOrders();
    }
}

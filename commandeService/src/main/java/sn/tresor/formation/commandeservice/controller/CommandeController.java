package sn.tresor.formation.commandeservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.tresor.formation.commandeservice.client.product.dto.ProductResponse;
import sn.tresor.formation.commandeservice.dto.OrderRequest;
import sn.tresor.formation.commandeservice.dto.OrderResponse;
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

    @GetMapping("/all-products")
    public Page<ProductResponse> getAllProduct() {
        return commandeService.getAllProducts();
    }

    @PostMapping
    public ResponseEntity<OrderResponse> addOrder(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(commandeService.createOrder(orderRequest));
    }
}

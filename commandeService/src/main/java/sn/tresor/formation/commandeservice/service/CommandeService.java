package sn.tresor.formation.commandeservice.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.tresor.formation.commandeservice.client.product.ProductServiceClient;
import sn.tresor.formation.commandeservice.client.product.dto.ProductResponse;
import sn.tresor.formation.commandeservice.dto.OrderItemRequest;
import sn.tresor.formation.commandeservice.dto.OrderItemResponse;
import sn.tresor.formation.commandeservice.dto.OrderRequest;
import sn.tresor.formation.commandeservice.dto.OrderResponse;
import sn.tresor.formation.commandeservice.model.Commande;
import sn.tresor.formation.commandeservice.model.DetailsCommande;
import sn.tresor.formation.commandeservice.repository.CommandeRepository;
import sn.tresor.formation.commandeservice.repository.DetailsCommandeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CommandeService {

    private final CommandeRepository commandeRepository;

    private final ProductServiceClient productServiceClient;
    private final DetailsCommandeRepository detailsCommandeRepository;

    public CommandeService(CommandeRepository commandeRepository, ProductServiceClient productServiceClient, DetailsCommandeRepository detailsCommandeRepository) {
        this.commandeRepository = commandeRepository;
        this.productServiceClient = productServiceClient;
        this.detailsCommandeRepository = detailsCommandeRepository;
    }

    public List<Commande> getAllOrders() {
        return commandeRepository.findAll();
    }

    public Page<ProductResponse> getAllProducts() {
        return productServiceClient.getAllProducts().getBody();
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {

        // Creation de l'entete de la commande
        Commande commande = new Commande(orderRequest.getCustomerId(), BigDecimal.ONE);
        commande = commandeRepository.save(commande);
        Commande savedCmd = null;
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        OrderResponse orderResponse = new OrderResponse();
        List<DetailsCommande> detailsCommandeList = new ArrayList<>();
        for (OrderItemRequest item : orderRequest.getItems()) {
            // Recuperation du produit depuis ProductService
            ProductResponse productResponse = productServiceClient.getProductById(item.getProductId()).getBody();

            if (productServiceClient.checkAvailability(item.getProductId()).getBody()) {
                DetailsCommande detailsCommande = new DetailsCommande();
                detailsCommande.setProductId(item.getProductId());
                detailsCommande.setQuantity(item.getQuantity());
                detailsCommande.setProductName(productResponse.getName());
                detailsCommande.setPrice(productResponse.getPrice());
                detailsCommande.setTotalPrice(new BigDecimal(productResponse.getPrice().doubleValue() * item.getQuantity()));
                commande.setTotalAmount(commande.getTotalAmount().add(detailsCommande.getTotalPrice()));
                detailsCommande.setCommande(commande);
                detailsCommandeList.add(detailsCommande);
                detailsCommandeRepository.save(detailsCommande);

                savedCmd = commandeRepository.save(commande);
            }

            if (savedCmd != null && !detailsCommandeList.isEmpty()) {
                orderResponse = new OrderResponse(savedCmd);
            }
        }
        return orderResponse;
    }
}

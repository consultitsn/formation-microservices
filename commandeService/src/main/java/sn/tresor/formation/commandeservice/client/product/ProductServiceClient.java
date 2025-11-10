package sn.tresor.formation.commandeservice.client.product;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sn.tresor.formation.commandeservice.client.product.dto.ProductResponse;

@FeignClient(
        name = "product-service"
)
public interface ProductServiceClient {

    @GetMapping("/api/v1/products/{id}")
    ResponseEntity<ProductResponse> getProductById(@PathVariable Long id);

    @GetMapping("/api/v1/products")
    ResponseEntity<Page<ProductResponse>> getAllProducts();

    @GetMapping("/api/v1/products/{id}/availability")
    ResponseEntity<Boolean> checkAvailability(@PathVariable Long id);
}

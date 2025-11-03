package com.qualimark.ecommerce.productService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);

		System.out.println("🚀 Application Microservices Introduction démarrée !");
		System.out.println("📚 Consultez la documentation : http://localhost:8080/swagger-ui.html");
		System.out.println("🗄️  Console H2 : http://localhost:8080/h2-console");
		System.out.println("📊 Actuator : http://localhost:8080/actuator");
	}

}

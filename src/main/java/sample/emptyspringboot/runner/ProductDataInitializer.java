package sample.emptyspringboot.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import eval.sample.model.Product;
import eval.sample.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Configuration class that provides a CommandLineRunner bean to initialize
 * product data and demonstrate repository operations.
 */
@Configuration
public class ProductDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ProductDataInitializer.class);

    /**
     * Creates a CommandLineRunner bean that initializes the database with sample products
     * and demonstrates various repository operations.
     *
     * @param repository the product repository
     * @return a CommandLineRunner instance
     */
    @Bean
    public CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            logger.info("Starting database initialization...");

            // Create and save sample products
            Product laptop = new Product("Laptop", "High-performance laptop with 16GB RAM", new BigDecimal("1299.99"));
            Product smartphone = new Product("Smartphone", "Latest model with 128GB storage", new BigDecimal("899.99"));
            Product tablet = new Product("Tablet", "10-inch tablet with retina display", new BigDecimal("499.99"));
            Product headphones = new Product("Headphones", "Noise-cancelling wireless headphones", new BigDecimal("199.99"));
            Product smartwatch = new Product("Smartwatch", "Fitness tracker with heart rate monitor", new BigDecimal("249.99"));

            logger.info("Saving sample products...");
            repository.saveAll(List.of(laptop, smartphone, tablet, headphones, smartwatch));

            // Demonstrate findAll operation
            logger.info("Finding all products:");
            repository.findAll().forEach(product -> logger.info(product.toString()));

            // Demonstrate findById operation
            logger.info("Finding product by ID 1:");
            Optional<Product> foundProduct = repository.findById(1L);
            foundProduct.ifPresent(product -> logger.info(product.toString()));

            // Demonstrate findByName operation
            logger.info("Finding product by name 'Laptop':");
            Optional<Product> laptopProduct = repository.findByName("Laptop");
            laptopProduct.ifPresent(product -> logger.info(product.toString()));

            // Demonstrate findByPriceGreaterThan operation
            logger.info("Finding products with price greater than $500:");
            List<Product> expensiveProducts = repository.findByPriceGreaterThan(new BigDecimal("500"));
            expensiveProducts.forEach(product -> logger.info(product.toString()));

            // Demonstrate findByPriceLessThan operation
            logger.info("Finding products with price less than $300:");
            List<Product> affordableProducts = repository.findByPriceLessThan(new BigDecimal("300"));
            affordableProducts.forEach(product -> logger.info(product.toString()));

            // Demonstrate findByNameContainingIgnoreCase operation
            logger.info("Finding products with 'phone' in the name:");
            List<Product> phoneProducts = repository.findByNameContainingIgnoreCase("phone");
            phoneProducts.forEach(product -> logger.info(product.toString()));

            // Demonstrate findByPriceRange operation
            logger.info("Finding products with price between $200 and $900:");
            List<Product> midRangeProducts = repository.findByPriceRange(new BigDecimal("200"), new BigDecimal("900"));
            midRangeProducts.forEach(product -> logger.info(product.toString()));

            // Demonstrate searchByKeyword operation
            logger.info("Searching products with keyword 'wireless':");
            List<Product> wirelessProducts = repository.searchByKeyword("wireless");
            wirelessProducts.forEach(product -> logger.info(product.toString()));

            // Demonstrate update operation
            logger.info("Updating product with ID 1...");
            foundProduct.ifPresent(product -> {
                product.setPrice(new BigDecimal("1199.99"));
                repository.save(product);
                logger.info("Updated product: " + product);
            });

            // Demonstrate delete operation
            logger.info("Deleting product with ID 5...");
            repository.deleteById(5L);
            
            // Verify deletion
            logger.info("Verifying deletion - Finding all products after deletion:");
            repository.findAll().forEach(product -> logger.info(product.toString()));

            logger.info("Database initialization completed.");
        };
    }
}
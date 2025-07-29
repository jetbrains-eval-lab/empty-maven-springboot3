package eval.sample.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import eval.sample.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the ProductRepository.
 * Uses @DataJpaTest to set up an in-memory database for testing.
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should save a product")
    void shouldSaveProduct() {
        // Given
        Product product = new Product("Test Product", "Test Description", new BigDecimal("99.99"));

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        assertNotNull(savedProduct.getId(), "ID should be generated");
        assertEquals("Test Product", savedProduct.getName(), "Name should match");
        assertEquals("Test Description", savedProduct.getDescription(), "Description should match");
        assertEquals(new BigDecimal("99.99"), savedProduct.getPrice(), "Price should match");
    }

    @Test
    @DisplayName("Should find a product by ID")
    void shouldFindProductById() {
        // Given
        Product product = new Product("Test Product", "Test Description", new BigDecimal("99.99"));
        entityManager.persist(product);
        entityManager.flush();

        // When
        Optional<Product> foundProduct = productRepository.findById(product.getId());

        // Then
        assertTrue(foundProduct.isPresent(), "Product should be found");
        assertEquals(product.getId(), foundProduct.get().getId(), "ID should match");
        assertEquals(product.getName(), foundProduct.get().getName(), "Name should match");
    }

    @Test
    @DisplayName("Should find a product by name")
    void shouldFindProductByName() {
        // Given
        Product product1 = new Product("Laptop", "Test Description", new BigDecimal("999.99"));
        Product product2 = new Product("Smartphone", "Test Description", new BigDecimal("499.99"));
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        // When
        Optional<Product> foundProduct = productRepository.findByName("Laptop");

        // Then
        assertTrue(foundProduct.isPresent(), "Product should be found");
        assertEquals("Laptop", foundProduct.get().getName(), "Name should match");
    }

    @Test
    @DisplayName("Should find products with price greater than")
    void shouldFindProductsWithPriceGreaterThan() {
        // Given
        Product product1 = new Product("Laptop", "Test Description", new BigDecimal("999.99"));
        Product product2 = new Product("Smartphone", "Test Description", new BigDecimal("499.99"));
        Product product3 = new Product("Headphones", "Test Description", new BigDecimal("99.99"));
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // When
        List<Product> expensiveProducts = productRepository.findByPriceGreaterThan(new BigDecimal("500"));

        // Then
        assertEquals(1, expensiveProducts.size(), "Should find 1 product");
        assertEquals("Laptop", expensiveProducts.get(0).getName(), "Should find the laptop");
    }

    @Test
    @DisplayName("Should find products with price less than")
    void shouldFindProductsWithPriceLessThan() {
        // Given
        Product product1 = new Product("Laptop", "Test Description", new BigDecimal("999.99"));
        Product product2 = new Product("Smartphone", "Test Description", new BigDecimal("499.99"));
        Product product3 = new Product("Headphones", "Test Description", new BigDecimal("99.99"));
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // When
        List<Product> affordableProducts = productRepository.findByPriceLessThan(new BigDecimal("200"));

        // Then
        assertEquals(1, affordableProducts.size(), "Should find 1 product");
        assertEquals("Headphones", affordableProducts.get(0).getName(), "Should find the headphones");
    }

    @Test
    @DisplayName("Should find products with name containing")
    void shouldFindProductsWithNameContaining() {
        // Given
        Product product1 = new Product("Laptop", "Test Description", new BigDecimal("999.99"));
        Product product2 = new Product("Smartphone", "Test Description", new BigDecimal("499.99"));
        Product product3 = new Product("Wireless Headphones", "Test Description", new BigDecimal("99.99"));
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // When
        List<Product> phoneProducts = productRepository.findByNameContainingIgnoreCase("phone");

        // Then
        assertEquals(2, phoneProducts.size(), "Should find 2 products");
        assertTrue(phoneProducts.stream().anyMatch(p -> p.getName().equals("Smartphone")), "Should find the smartphone");
        assertTrue(phoneProducts.stream().anyMatch(p -> p.getName().equals("Wireless Headphones")), "Should find the headphones");
    }

    @Test
    @DisplayName("Should find products with description containing")
    void shouldFindProductsWithDescriptionContaining() {
        // Given
        Product product1 = new Product("Laptop", "High-performance device", new BigDecimal("999.99"));
        Product product2 = new Product("Smartphone", "Portable device", new BigDecimal("499.99"));
        Product product3 = new Product("Headphones", "Audio accessory", new BigDecimal("99.99"));
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // When
        List<Product> deviceProducts = productRepository.findByDescriptionContainingIgnoreCase("device");

        // Then
        assertEquals(2, deviceProducts.size(), "Should find 2 products");
        assertTrue(deviceProducts.stream().anyMatch(p -> p.getName().equals("Laptop")), "Should find the laptop");
        assertTrue(deviceProducts.stream().anyMatch(p -> p.getName().equals("Smartphone")), "Should find the smartphone");
    }

    @Test
    @DisplayName("Should find products within price range")
    void shouldFindProductsWithinPriceRange() {
        // Given
        Product product1 = new Product("Laptop", "Test Description", new BigDecimal("999.99"));
        Product product2 = new Product("Smartphone", "Test Description", new BigDecimal("499.99"));
        Product product3 = new Product("Headphones", "Test Description", new BigDecimal("99.99"));
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // When
        List<Product> midRangeProducts = productRepository.findByPriceRange(new BigDecimal("100"), new BigDecimal("500"));

        // Then
        assertEquals(1, midRangeProducts.size(), "Should find 1 product");
        assertEquals("Smartphone", midRangeProducts.get(0).getName(), "Should find the smartphone");
    }

    @Test
    @DisplayName("Should search products by keyword")
    void shouldSearchProductsByKeyword() {
        // Given
        Product product1 = new Product("Laptop", "High-performance device", new BigDecimal("999.99"));
        Product product2 = new Product("Smartphone", "Portable device", new BigDecimal("499.99"));
        Product product3 = new Product("Wireless Headphones", "Bluetooth audio accessory", new BigDecimal("99.99"));
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // When
        List<Product> wirelessProducts = productRepository.searchByKeyword("wireless");

        // Then
        assertEquals(1, wirelessProducts.size(), "Should find 1 product");
        assertEquals("Wireless Headphones", wirelessProducts.get(0).getName(), "Should find the wireless headphones");

        // When searching for a term that appears in both name and description
        List<Product> deviceProducts = productRepository.searchByKeyword("device");

        // Then
        assertEquals(2, deviceProducts.size(), "Should find 2 products");
        assertTrue(deviceProducts.stream().anyMatch(p -> p.getName().equals("Laptop")), "Should find the laptop");
        assertTrue(deviceProducts.stream().anyMatch(p -> p.getName().equals("Smartphone")), "Should find the smartphone");
    }

    @Test
    @DisplayName("Should update a product")
    void shouldUpdateProduct() {
        // Given
        Product product = new Product("Laptop", "Test Description", new BigDecimal("999.99"));
        entityManager.persist(product);
        entityManager.flush();

        // When
        product.setPrice(new BigDecimal("899.99"));
        Product updatedProduct = productRepository.save(product);

        // Then
        assertEquals(new BigDecimal("899.99"), updatedProduct.getPrice(), "Price should be updated");

        // Verify in the database
        Product retrievedProduct = entityManager.find(Product.class, product.getId());
        assertEquals(new BigDecimal("899.99"), retrievedProduct.getPrice(), "Price should be updated in the database");
    }

    @Test
    @DisplayName("Should delete a product")
    void shouldDeleteProduct() {
        // Given
        Product product = new Product("Laptop", "Test Description", new BigDecimal("999.99"));
        entityManager.persist(product);
        entityManager.flush();

        // When
        productRepository.deleteById(product.getId());

        // Then
        Product retrievedProduct = entityManager.find(Product.class, product.getId());
        assertNull(retrievedProduct, "Product should be deleted from the database");
    }
}
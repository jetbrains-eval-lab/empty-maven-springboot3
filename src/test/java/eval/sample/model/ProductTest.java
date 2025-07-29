package eval.sample.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Product entity class.
 */
class ProductTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create a valid product")
    void shouldCreateValidProduct() {
        // Given
        Product product = new Product("Laptop", "High-performance laptop", new BigDecimal("1299.99"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.isEmpty(), "No validation errors should be present");
    }

    @Test
    @DisplayName("Should detect null name")
    void shouldDetectNullName() {
        // Given
        Product product = new Product(null, "Description", new BigDecimal("99.99"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty(), "Validation errors should be present");
        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("Product name is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should detect empty name")
    void shouldDetectEmptyName() {
        // Given
        Product product = new Product("", "Description", new BigDecimal("99.99"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty(), "Validation errors should be present");
        assertEquals(2, violations.size(), "Should have 2 validation errors");
        // An empty string violates both @NotBlank and @Size(min=2) constraints
        assertTrue(
            violations.stream()
                .map(ConstraintViolation::getMessage)
                .anyMatch(message -> message.equals("Product name is required")),
            "Should contain 'Product name is required' message"
        );
    }

    @Test
    @DisplayName("Should detect name too short")
    void shouldDetectNameTooShort() {
        // Given
        Product product = new Product("A", "Description", new BigDecimal("99.99"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty(), "Validation errors should be present");
        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("Product name must be between 2 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should detect name too long")
    void shouldDetectNameTooLong() {
        // Given
        String longName = "A".repeat(101); // 101 characters
        Product product = new Product(longName, "Description", new BigDecimal("99.99"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty(), "Validation errors should be present");
        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("Product name must be between 2 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should detect description too long")
    void shouldDetectDescriptionTooLong() {
        // Given
        String longDescription = "A".repeat(501); // 501 characters
        Product product = new Product("Product", longDescription, new BigDecimal("99.99"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty(), "Validation errors should be present");
        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("Description cannot exceed 500 characters", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should detect null price")
    void shouldDetectNullPrice() {
        // Given
        Product product = new Product("Product", "Description", null);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty(), "Validation errors should be present");
        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("Price is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should detect price too low")
    void shouldDetectPriceTooLow() {
        // Given
        Product product = new Product("Product", "Description", new BigDecimal("0.00"));
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty(), "Validation errors should be present");
        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("Price must be greater than zero", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsAndHashCode() {
        // Given
        Product product1 = new Product(1L, "Laptop", "Description", new BigDecimal("1299.99"));
        Product product2 = new Product(1L, "Laptop", "Description", new BigDecimal("1299.99"));
        Product product3 = new Product(2L, "Laptop", "Description", new BigDecimal("1299.99"));
        
        // Then
        assertEquals(product1, product2, "Products with same ID and attributes should be equal");
        assertNotEquals(product1, product3, "Products with different IDs should not be equal");
        assertEquals(product1.hashCode(), product2.hashCode(), "Hash codes should be equal for equal products");
        assertNotEquals(product1.hashCode(), product3.hashCode(), "Hash codes should differ for different products");
    }

    @Test
    @DisplayName("Should test toString")
    void shouldTestToString() {
        // Given
        Product product = new Product(1L, "Laptop", "Description", new BigDecimal("1299.99"));
        
        // When
        String toString = product.toString();
        
        // Then
        assertTrue(toString.contains("id=1"), "toString should contain the ID");
        assertTrue(toString.contains("name='Laptop'"), "toString should contain the name");
        assertTrue(toString.contains("description='Description'"), "toString should contain the description");
        assertTrue(toString.contains("price=1299.99"), "toString should contain the price");
    }
}
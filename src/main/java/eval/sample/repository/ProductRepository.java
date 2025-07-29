package eval.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import eval.sample.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity.
 * Extends JpaRepository to inherit basic CRUD operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find a product by its name.
     *
     * @param name the product name
     * @return an Optional containing the product if found, or empty if not found
     */
    Optional<Product> findByName(String name);

    /**
     * Find products with a price greater than the specified value.
     *
     * @param price the minimum price
     * @return a list of products with prices greater than the specified value
     */
    List<Product> findByPriceGreaterThan(BigDecimal price);

    /**
     * Find products with a price less than the specified value.
     *
     * @param price the maximum price
     * @return a list of products with prices less than the specified value
     */
    List<Product> findByPriceLessThan(BigDecimal price);

    /**
     * Find products with a name containing the specified string (case-insensitive).
     *
     * @param keyword the keyword to search for in product names
     * @return a list of products with names containing the keyword
     */
    List<Product> findByNameContainingIgnoreCase(String keyword);

    /**
     * Find products with a description containing the specified string (case-insensitive).
     *
     * @param keyword the keyword to search for in product descriptions
     * @return a list of products with descriptions containing the keyword
     */
    List<Product> findByDescriptionContainingIgnoreCase(String keyword);

    /**
     * Custom JPQL query to find products within a price range.
     *
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return a list of products with prices within the specified range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price ASC")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Custom JPQL query to find products with names or descriptions containing the keyword.
     *
     * @param keyword the keyword to search for
     * @return a list of products matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.createdAt >= :threeDaysAgo ORDER BY p.totalSales DESC")
    List<Product> findTop5BySales(@Param("threeDaysAgo") LocalDateTime threeDaysAgo);
}

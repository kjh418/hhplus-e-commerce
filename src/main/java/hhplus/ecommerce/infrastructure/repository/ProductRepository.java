package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.application.product.dto.TopProductResponse;
import hhplus.ecommerce.domain.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new hhplus.ecommerce.application.product.dto.TopProductResponse(od.productId, p.name, p.price, CAST(SUM(od.quantity) AS int)) " +
            "FROM PaymentHistory ph " +
            "JOIN Orders o ON ph.orderId = o.id " +
            "JOIN OrdersDetail od ON o.id = od.orderId " +
            "JOIN Product p ON od.productId = p.id " +
            "WHERE ph.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY od.productId, p.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<TopProductResponse> findTopBySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdForUpdate(@Param("productId") Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdForPessimisticLock(@Param("productId") Long productId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdForOptimisticLock(@Param("productId") Long productId);
}

package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.cart.CartItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long id);

    // 비관적락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CartItem c WHERE c.cartId = :id AND c.productId = :productId")
    Optional<CartItem> findByCartIdAndProductId(Long id, Long productId);
}

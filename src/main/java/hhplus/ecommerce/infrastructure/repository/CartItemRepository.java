package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}

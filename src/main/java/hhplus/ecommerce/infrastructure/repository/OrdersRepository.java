package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}

package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.order.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> {
}

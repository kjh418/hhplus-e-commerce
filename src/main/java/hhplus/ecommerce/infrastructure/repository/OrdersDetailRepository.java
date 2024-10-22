package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.order.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> {
    List<OrdersDetail> findByOrderId(Long id);
}

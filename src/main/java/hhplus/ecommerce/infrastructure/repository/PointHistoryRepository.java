package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.payment.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}

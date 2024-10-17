package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.payment.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByUserId(Long userId);
}

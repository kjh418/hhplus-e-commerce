package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.payment.PointAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointAccountRepository extends JpaRepository<PointAccount, Integer> {
    Optional<PointAccount> findByUserId(Long userId);
}

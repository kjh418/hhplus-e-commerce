package hhplus.ecommerce.infrastructure.repository;

import hhplus.ecommerce.domain.payment.PointAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface UserPointRepository extends JpaRepository<PointAccount, Long> {
    @Query("SELECT p.balance FROM PointAccount p WHERE p.userId = :userId")
    BigDecimal findCurrentPointsByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE PointAccount p SET p.balance = :newBalance WHERE p.userId = :userId")
    void updatePoints(@Param("userId") Long userId, @Param("newBalance") BigDecimal newBalance);
}

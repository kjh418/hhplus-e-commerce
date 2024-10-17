package hhplus.ecommerce.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private BigDecimal points;
    private PointType type;
    private LocalDateTime createdAt;

    public PaymentHistory(Long userId, BigDecimal points, PointType type, LocalDateTime createdAt) {
        this.userId = userId;
        this.points = points;
        this.type = type;
        this.createdAt = createdAt;
    }
}

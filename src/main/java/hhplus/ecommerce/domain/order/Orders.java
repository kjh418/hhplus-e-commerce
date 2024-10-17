package hhplus.ecommerce.domain.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;
    
    public void completeOrder() {
        if (this.status != OrderStatus.COMPLETED) {
            this.status = OrderStatus.COMPLETED;
        }
    }

    public Orders(Long userId, BigDecimal totalAmount, OrderStatus status, LocalDateTime createdAt) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }
}

package hhplus.ecommerce.domain.order;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    private BigDecimal totalAmount;

    private String status;

    private LocalDateTime createdAt;

    protected Order() {
    }
}

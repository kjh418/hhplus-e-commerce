package hhplus.ecommerce.domain.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

    private Long orderId;

    private Long productId;

    private int quantity;

    private BigDecimal price;

    protected OrderDetail() {
    }
}

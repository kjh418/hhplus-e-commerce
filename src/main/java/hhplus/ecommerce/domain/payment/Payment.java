package hhplus.ecommerce.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long orderId;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    protected Payment() {
    }
}

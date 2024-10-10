package hhplus.ecommerce.domain.cart;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    private Long userId;

    private LocalDateTime createdAt;

    protected Cart() {
    }
}

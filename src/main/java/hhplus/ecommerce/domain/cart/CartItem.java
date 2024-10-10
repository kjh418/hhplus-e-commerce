package hhplus.ecommerce.domain.cart;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    private Long cartId;

    private Long productId;

    private int quantity;

    private String options;

    private boolean isSelected;

    private LocalDateTime addedAt;

    protected CartItem() {
    }
}

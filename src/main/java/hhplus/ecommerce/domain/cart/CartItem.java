package hhplus.ecommerce.domain.cart;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cartId;
    private Long productId;
    private int quantity;
    private boolean isSelected;
    private LocalDateTime addedAt;

    public CartItem(Long cartId, Long productId, int quantity, boolean isSelected, LocalDateTime addedAt) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.isSelected = isSelected;
        this.addedAt = addedAt;
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }
}

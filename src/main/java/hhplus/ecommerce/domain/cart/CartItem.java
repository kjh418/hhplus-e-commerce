package hhplus.ecommerce.domain.cart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cartId;

    private Long productId;

    private int quantity;

    @Convert(converter = OptionConverter.class)
    private Option options;

    private boolean isSelected;

    private LocalDateTime addedAt;
}

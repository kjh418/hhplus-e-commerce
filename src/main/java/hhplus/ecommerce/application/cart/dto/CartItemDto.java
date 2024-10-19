package hhplus.ecommerce.application.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private int quantity;
    private boolean isSelected;
    private LocalDateTime addedAt;
    private String options;
}

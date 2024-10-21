package hhplus.ecommerce.application.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    @NotNull
    private Long cartItemId;

    @NotNull
    private Long cartId;

    @NotNull
    private Long productId;

    @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다.")
    private int quantity;

    private boolean isSelected;

    private LocalDateTime addedAt;
    
    private String options;
}

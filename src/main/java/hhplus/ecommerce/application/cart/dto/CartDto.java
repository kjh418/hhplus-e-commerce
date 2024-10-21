package hhplus.ecommerce.application.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    @NotNull
    private Long cartId;

    @NotNull
    private Long userId;

    private LocalDateTime createdAt;
}

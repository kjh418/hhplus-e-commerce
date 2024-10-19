package hhplus.ecommerce.application.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long cartId;
    private Long userId;
    private LocalDateTime createdAt;
}

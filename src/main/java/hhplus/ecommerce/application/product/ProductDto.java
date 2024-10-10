package hhplus.ecommerce.application.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    private String name;
    private BigDecimal price;
    private int stockQuantity;
    private String description;
    private LocalDateTime createdAt;
}

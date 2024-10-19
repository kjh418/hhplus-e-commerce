package hhplus.ecommerce.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {
    private Long productId;
    private String name;
    private BigDecimal price;
    private int stockQuantity;
    private String description;
}
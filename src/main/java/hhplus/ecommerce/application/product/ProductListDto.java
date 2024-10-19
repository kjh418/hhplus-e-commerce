package hhplus.ecommerce.application.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDto {
    private Long productId;
    private String name;
    private BigDecimal price;
}

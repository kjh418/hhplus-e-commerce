package hhplus.ecommerce.application.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {
    @NotNull(message = "상품 ID는 필수 값입니다.")
    private Long productId;

    @NotBlank(message = "상품 이름은 필수 값입니다.")
    private String name;

    @NotNull(message = "상품 가격은 필수 값입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "상품 가격은 0보다 커야 합니다.")
    private BigDecimal price;

    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private int stockQuantity;
    
    private String description;
}
package hhplus.ecommerce.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PopularProductDto {
    private Long id;
    private Long productId;
    private Long totalSales;
    private Long viewCount;
    private BigDecimal avgRating;
    private Long orderCount;
    private LocalDateTime lastUpdated;
}

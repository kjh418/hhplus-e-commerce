package hhplus.ecommerce.application.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PopularProductResponse {
    private Long productId;
    private String name;
    private BigDecimal price;
    //private Long totalSales;
    //private Long viewCount;
    //private BigDecimal avgRating;
    private Long orderCount;
}

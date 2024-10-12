package hhplus.ecommerce.application.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long productId;
    private int quantity;
    private BigDecimal price;
}

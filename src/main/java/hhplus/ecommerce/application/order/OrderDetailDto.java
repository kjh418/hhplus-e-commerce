package hhplus.ecommerce.application.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private Long orderDetailId;
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal price;
}

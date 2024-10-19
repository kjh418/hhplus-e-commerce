package hhplus.ecommerce.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Top5ProductResponse {
    private Long productId;
    private String name;
    private BigDecimal price;
    private int totalSales; // 판매량 포함
}

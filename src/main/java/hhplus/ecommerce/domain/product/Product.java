package hhplus.ecommerce.domain.product;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId ;

    private String name;

    private String description;

    private BigDecimal price;

    private int stockQuantity;

    private LocalDateTime createdAt;

    // TODO : 재고가 요청 개수보다 작을 경우 '재고가 부족합니다' 처리하기

    public Product() {}
}

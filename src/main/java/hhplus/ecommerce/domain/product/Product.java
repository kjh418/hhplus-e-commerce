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
    private Long id ;

    private String name;

    private String description;

    private BigDecimal price;

    private int stockQuantity;

    private LocalDateTime createdAt;

    public Product() {}
}

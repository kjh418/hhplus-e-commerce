package hhplus.ecommerce.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
public class PopularProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long popularProductId;

    private Long productId;

    private int totalSales;

    private int viewCount;

    private BigDecimal avgRating;

    private int orderCount;

    private LocalDateTime lastUpdated;

    protected PopularProduct() {}
}

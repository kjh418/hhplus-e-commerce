package hhplus.ecommerce.domain.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
public class PopularProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private int totalSales;

    private int viewCount;

    private BigDecimal avgRating;

    private int orderCount;

    private LocalDateTime lastUpdated;
}

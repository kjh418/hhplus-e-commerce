package hhplus.ecommerce.domain.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private static final Logger logger = LoggerFactory.getLogger(Product.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private int stockQuantity;

    private LocalDateTime createdAt;

    private int totalSales;

    @Version
    private Integer version;
    
    public void reduceStock(int quantity) {
        if (this.stockQuantity < quantity) {
            logger.warn("재고 부족 발생 - 상품 ID: {}, 현재 재고: {}", this.id, this.stockQuantity);
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.stockQuantity);
        }
        this.stockQuantity -= quantity;
        logger.info("재고 차감 완료 - 상품 ID: {}, 남은 재고: {}", this.id, this.stockQuantity);
    }

    public void increaseSales(int quantity) {
        this.totalSales += quantity;
    }
}

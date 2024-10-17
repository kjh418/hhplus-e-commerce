package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.domain.payment.PointType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDto {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private PointType type;
    private LocalDateTime createdAt;
}

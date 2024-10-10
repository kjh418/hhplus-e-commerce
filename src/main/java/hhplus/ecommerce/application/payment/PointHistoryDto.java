package hhplus.ecommerce.application.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDto {
    private Long pointHistoryId;
    private Long userId;
    private BigDecimal points;
    private String description;
    private String type;
    private LocalDateTime createdAt;
}

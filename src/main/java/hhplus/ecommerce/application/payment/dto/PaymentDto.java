package hhplus.ecommerce.application.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long paymentId;
    private Long orderId;
    @NotNull(message = "충전 금액은 필수 값입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "충전할 금액은 0보다 커야 합니다.")
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public PaymentDto(BigDecimal amount) {
        this.amount = amount;
    }

}

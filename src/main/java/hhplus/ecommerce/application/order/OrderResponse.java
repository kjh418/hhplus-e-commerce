package hhplus.ecommerce.application.order;

import hhplus.ecommerce.application.common.OrderPaymentStatus;
import hhplus.ecommerce.application.user.UserBalanceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderPaymentStatus orderPaymentStatus;
    private LocalDateTime createdAt;
    private List<OrderDetailResponse> orderDetails;

    private BigDecimal paymentAmount;
    private OrderPaymentStatus paymentStatus;
    private LocalDateTime paymentDate;

    private UserBalanceResponse userBalanceResponse;
}

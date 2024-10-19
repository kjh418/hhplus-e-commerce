package hhplus.ecommerce.application.order.dro;

import hhplus.ecommerce.application.user.dto.UserBalanceResponse;
import hhplus.ecommerce.domain.order.OrderStatus;
import hhplus.ecommerce.domain.payment.PaymentStatus;
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
    private OrderStatus orderStatus; // 주문 상태
    private LocalDateTime createdAt;
    private List<OrderDetailResponse> orderDetails;

    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus; // 결제 상태
    private LocalDateTime paymentDate;

    private UserBalanceResponse userBalanceResponse;
}

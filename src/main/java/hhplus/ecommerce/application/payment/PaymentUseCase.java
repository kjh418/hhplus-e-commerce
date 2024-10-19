package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentUseCase {

    private final PaymentService paymentService;

    // 결제 처리
    public PaymentResponse processPayment(Long orderId, Long userId, BigDecimal paymentAmount) {
        return paymentService.processPayment(orderId, userId, paymentAmount);
    }
}

package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.payment.dto.PaymentDto;
import hhplus.ecommerce.application.user.dto.UserBalanceResponse;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountPointUseCase {

    private final AccountPointService accountPointService;

    // 잔액 조회
    public UserBalanceResponse getBalance(Long userId) {
        return accountPointService.getBalance(userId);
    }

    // 포인트 충전
    public UserBalanceResponse chargePoints(Long userId, PaymentDto paymentDto, Long orderId) {
        return accountPointService.chargePoints(userId, paymentDto, orderId);
    }

    // 포인트 이력 조회
    public List<PaymentHistory> getPaymentHistory(Long userId) {
        return accountPointService.getPaymentHistory(userId);
    }
}
